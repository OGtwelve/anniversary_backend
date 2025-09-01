package org.zhejianglab.dxjh.modules.annivquiz.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zhejianglab.dxjh.modules.annivquiz.dto.QuizDto;
import org.zhejianglab.dxjh.modules.annivquiz.dto.QuizValidateRequest;
import org.zhejianglab.dxjh.modules.annivquiz.dto.QuizValidateResultDto;
import org.zhejianglab.dxjh.modules.annivquiz.entity.*;
import org.zhejianglab.dxjh.modules.annivquiz.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author :og-twelve
 * @date : 2025/9/1
 */
@Service
public class AnnivQuizService {

    private final AnnivQuizRepository quizRepo;
    private final AnnivQuizQuestionRepository qRepo;
    private final AnnivQuizOptionRepository oRepo;
    private final AnnivQuizPassTicketRepository ticketRepo;
    private final ObjectMapper om = new ObjectMapper();

    public AnnivQuizService(AnnivQuizRepository quizRepo,
                            AnnivQuizQuestionRepository qRepo,
                            AnnivQuizOptionRepository oRepo,
                            AnnivQuizPassTicketRepository ticketRepo) {
        this.quizRepo = quizRepo;
        this.qRepo = qRepo;
        this.oRepo = oRepo;
        this.ticketRepo = ticketRepo;
    }

    /** 获取当前激活问卷（不包含正确答案） */
    @Transactional(readOnly = true)
    public QuizDto getActiveQuiz() {
        AnnivQuiz quiz = quizRepo.findFirstByIsActiveTrue()
                .orElseThrow(() -> new IllegalStateException("暂无可用问卷"));
        List<AnnivQuizQuestion> qs = qRepo.findByQuizIdOrderByIdxNoAsc(quiz.getId());

        QuizDto dto = new QuizDto();
        dto.setQuizCode(quiz.getQuizCode());
        dto.setTitle(quiz.getTitle());

        List<QuizDto.Question> qDtos = new ArrayList<>();
        for (AnnivQuizQuestion q : qs) {
            QuizDto.Question qd = new QuizDto.Question();
            qd.setId(q.getId());
            qd.setIdxNo(q.getIdxNo());
            qd.setContent(q.getContent());

            List<AnnivQuizOption> options = oRepo.findByQuestionIdOrderByIdxNoAsc(q.getId());
            List<QuizDto.Option> oDtos = options.stream().map(op -> {
                QuizDto.Option od = new QuizDto.Option();
                od.setId(op.getId());
                od.setIdxNo(op.getIdxNo());
                od.setContent(op.getContent());
                return od;
            }).collect(Collectors.toList());

            qd.setOptions(oDtos);
            qDtos.add(qd);
        }
        dto.setQuestions(qDtos);
        return dto;
    }

    /** 判题；全对则生成一次性通行令牌（默认10分钟有效） */
    @Transactional
    public QuizValidateResultDto validate(String quizCode, List<QuizValidateRequest.Answer> answers, String ip, String ua) {
        AnnivQuiz quiz = quizRepo.findByQuizCode(quizCode)
                .orElseThrow(() -> new IllegalStateException("问卷不存在"));

        List<AnnivQuizQuestion> qs = qRepo.findByQuizIdOrderByIdxNoAsc(quiz.getId());
        Map<Long, List<AnnivQuizOption>> optMap = new HashMap<>();
        for (AnnivQuizQuestion q : qs) {
            optMap.put(q.getId(), oRepo.findByQuestionIdOrderByIdxNoAsc(q.getId()));
        }

        Map<Long, Long> chosen = new HashMap<>();
        for (QuizValidateRequest.Answer a : answers) {
            chosen.put(a.getQuestionId(), a.getOptionId());
        }

        List<QuizValidateResultDto.Item> items = new ArrayList<>();
        int correct = 0;
        for (AnnivQuizQuestion q : qs) {
            Long picked = chosen.get(q.getId());
            boolean ok = false;
            if (picked != null) {
                for (AnnivQuizOption op : optMap.getOrDefault(q.getId(), Collections.emptyList())) {
                    if (op.getId().equals(picked)) {
                        ok = Boolean.TRUE.equals(op.getIsCorrect());
                        break;
                    }
                }
            }
            items.add(new QuizValidateResultDto.Item(q.getId(), ok));
            if (ok) correct++;
        }
        boolean allCorrect = (correct == qs.size());
        if (!allCorrect) {
            return new QuizValidateResultDto(false, items, null, null);
        }

        // 全对：生成一次性通行令牌（10分钟有效）
        String token = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expire = now.plusMinutes(10);

        AnnivQuizPassTicket t = new AnnivQuizPassTicket();
        t.setQuiz(quiz);
        t.setToken(token);
        t.setExpiresAt(expire);
        t.setIp(ip);
        t.setUa(ua);
        try {
            t.setAnswerJson(om.writeValueAsString(chosen));
        } catch (Exception ignore) {}
        t.setCorrectCount(correct);
        t.setAllCorrect(true);
        ticketRepo.save(t);

        return new QuizValidateResultDto(true, items, token, expire);
    }

    /** 证书签发前消费通行令牌（一次性） */
    @Transactional
    public void consumePassToken(String token, String ip, String ua) {
        AnnivQuizPassTicket t = ticketRepo.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("问卷通行令牌无效"));
        if (!Boolean.TRUE.equals(t.getAllCorrect())) throw new IllegalStateException("问卷未全部正确");
        if (Boolean.TRUE.equals(t.getUsedForCert())) throw new IllegalStateException("通行令牌已使用");
        if (t.getExpiresAt() != null && t.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new IllegalStateException("通行令牌已过期");

        // 可选：校验 IP/UA 一致（开启即取消注释）
        // if (t.getIp() != null && !Objects.equals(t.getIp(), ip)) throw new IllegalStateException("IP 不匹配");
        // if (t.getUa() != null && !Objects.equals(t.getUa(), ua)) throw new IllegalStateException("UA 不匹配");

        t.setUsedForCert(true);
        ticketRepo.save(t);
    }

}