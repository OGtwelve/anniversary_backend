package org.zhejianglab.dxjh.modules.annivquiz.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zhejianglab.dxjh.modules.annivquiz.dto.QuestionStatsDto;
import org.zhejianglab.dxjh.modules.annivquiz.dto.SurveyStatsDto;
import org.zhejianglab.dxjh.modules.annivquiz.entity.AnnivQuiz;
import org.zhejianglab.dxjh.modules.annivquiz.entity.AnnivQuizPassTicket;
import org.zhejianglab.dxjh.modules.annivquiz.entity.AnnivQuizQuestion;
import org.zhejianglab.dxjh.modules.annivquiz.repository.AnnivQuizOptionRepository;
import org.zhejianglab.dxjh.modules.annivquiz.repository.AnnivQuizPassTicketRepository;
import org.zhejianglab.dxjh.modules.annivquiz.repository.AnnivQuizQuestionRepository;
import org.zhejianglab.dxjh.modules.annivquiz.repository.AnnivQuizRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@Service
@Transactional(readOnly = true)
public class SurveyStatsService {

    private final AnnivQuizRepository quizRepo;
    private final AnnivQuizQuestionRepository questionRepo;
    private final AnnivQuizPassTicketRepository ticketRepo;
    private final AnnivQuizOptionRepository optionRepo;
    private final ObjectMapper objectMapper;

    public SurveyStatsService(AnnivQuizRepository quizRepo,
                              AnnivQuizQuestionRepository questionRepo,
                              AnnivQuizPassTicketRepository ticketRepo,
                              AnnivQuizOptionRepository optionRepo, ObjectMapper objectMapper
                              ) {
        this.quizRepo = quizRepo;
        this.questionRepo = questionRepo;
        this.ticketRepo = ticketRepo;
        this.optionRepo = optionRepo;
        this.objectMapper = objectMapper;
    }

    public SurveyStatsDto load() {
        SurveyStatsDto dto = new SurveyStatsDto();

        // 1) 当前生效的问卷
        AnnivQuiz quiz = quizRepo.findFirstByIsActiveTrueOrderByCreatedAtDesc().orElse(null);
        if (quiz == null) return dto;
        Long quizId = quiz.getId();

        // 2) 总参与 / 全对 / 今日答题
        long totalParticipants = ticketRepo.countByQuizId(quizId);
        long passedParticipants = ticketRepo.countByQuizIdAndAllCorrectTrue(quizId);

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end   = LocalDateTime.of(today, LocalTime.MAX);
        long todayAnswers   = ticketRepo.countByQuizIdAndCreatedAtBetween(quizId, start, end);

        dto.setTotalParticipants(totalParticipants);
        dto.setPassedParticipants(passedParticipants);
        dto.setPassRate(safePercent(passedParticipants, totalParticipants));
        dto.setTodayAnswers(todayAnswers);

        // 3) 平均分/正确率（不需要 totalCount 字段）
        long questionCount = questionRepo.countByQuizId(quizId);
        long sumCorrect    = ofNull(ticketRepo.sumCorrectCountByQuizId(quizId));

        double avgCorrectPerUser = totalParticipants > 0
                ? round1((double) sumCorrect / totalParticipants)
                : 0.0;

        double avgCorrectRatePct = (questionCount > 0 && totalParticipants > 0)
                ? round1(100.0 * sumCorrect / (questionCount * (double) totalParticipants))
                : 0.0;

        // 你前端目前没展示 averageScore；这里给“平均每人答对题数”
        dto.setAverageScore(avgCorrectPerUser);
        // 如果你更想展示百分比，把上一行换成：
        // dto.setAverageScore(avgCorrectRatePct);

        // 4) 题目列表（从 ticket.answerJson 聚合统计）
        List<AnnivQuizQuestion> qs = questionRepo.findByQuizIdOrderByIdxNoAsc(quizId);

        // 4.1 预置 DTO 容器（qid -> dto）
        Map<Long, QuestionStatsDto> qDtoMap = new LinkedHashMap<>();
        for (AnnivQuizQuestion q : qs) {
            QuestionStatsDto qd = new QuestionStatsDto(q.getId(), q.getContent());
            qd.setTotalAnswers(0);
            qd.setCorrectAnswers(0);
            qd.setCorrectRate(0);
            // 没有题目难度字段的话，用正确率阈值给标签，这里先默认 false，稍后算完再回填
            qd.setSimple(false);
            qDtoMap.put(q.getId(), qd);
        }

        // 4.2 查询每道题的“正确选项 id”
        List<Long> qids = qs.stream().map(AnnivQuizQuestion::getId).collect(Collectors.toList());
        Map<Long, Long> correctOptionIdByQid = new HashMap<>();
        if (!qids.isEmpty()) {
            for (Object[] row : optionRepo.findCorrectOptionPairs(qids)) {
                Long qid = (Long) row[0];
                Long oid = (Long) row[1];
                correctOptionIdByQid.put(qid, oid);
            }
        }

        // 4.3 遍历所有通行票据，解析 answerJson 计数
        List<AnnivQuizPassTicket> tickets = ticketRepo.findByQuizId(quizId);
        for (AnnivQuizPassTicket t : tickets) {
            String json = t.getAnswerJson();
            if (json == null || json.trim().isEmpty()) continue;

            try {
                // {"1": 1, "2": 4} -> Map<String, Long>
                Map<String, Long> ans = objectMapper.readValue(
                        json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Long>>() {}
                );

                for (Map.Entry<String, Long> e : ans.entrySet()) {
                    Long qid = null;
                    try {
                        qid = Long.valueOf(e.getKey());
                    } catch (NumberFormatException ignore) { }
                    if (qid == null) continue;

                    QuestionStatsDto qd = qDtoMap.get(qid);
                    if (qd == null) continue; // 非本问卷的题，跳过（或数据脏）

                    qd.setTotalAnswers(qd.getTotalAnswers() + 1);

                    Long chosenOptionId = e.getValue();
                    Long correctOptionId = correctOptionIdByQid.get(qid);
                    if (correctOptionId != null && correctOptionId.equals(chosenOptionId)) {
                        qd.setCorrectAnswers(qd.getCorrectAnswers() + 1);
                    }
                }
            } catch (Exception ex) {
                // 解析失败忽略该票据，避免统计中断
                // log.warn("Bad answerJson: {}", json, ex);
            }
        }

        // 4.4 计算每题正确率 & 难度标签（≥80% 记为“简单”）
        for (QuestionStatsDto qd : qDtoMap.values()) {
            long totalAns = qd.getTotalAnswers();
            long correctAns = qd.getCorrectAnswers();
            int rate = (totalAns > 0) ? (int) Math.round(correctAns * 100.0 / totalAns) : 0;
            qd.setCorrectRate(rate);
            qd.setSimple(rate >= 80); // 可按需调整阈值
        }

        dto.setQuestions(new ArrayList<>(qDtoMap.values()));

        return dto;
    }

    private long ofNull(Long v) { return v == null ? 0L : v; }

    private int safePercent(long a, long b) {
        if (b <= 0) return 0;
        return (int) Math.round((a * 100.0) / b);
    }

    private double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
