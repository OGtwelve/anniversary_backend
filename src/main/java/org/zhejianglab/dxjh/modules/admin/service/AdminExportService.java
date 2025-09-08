package org.zhejianglab.dxjh.modules.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivCertificate;
import org.zhejianglab.dxjh.modules.annivcert.repository.AnnivCertificateRepository;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */

@Service
@RequiredArgsConstructor
public class AdminExportService {

    private final AnnivCertificateRepository certRepo;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy/M/d");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss");

    // 列 key -> 中文表头
    private static final Map<String, String> HEADER_MAP = new LinkedHashMap<>();
    static {
        HEADER_MAP.put("fullNo",   "证书编号");
        HEADER_MAP.put("name",     "姓名");
        HEADER_MAP.put("workNo",   "工号");
        HEADER_MAP.put("startDate","入职时间");
        HEADER_MAP.put("workDays", "工龄(天)");
        HEADER_MAP.put("wishes",   "祝福语");
        HEADER_MAP.put("createdAt","首次注册时间");
    }

    // 列 key -> 提取函数
    private static final Map<String, Function<AnnivCertificate, String>> FIELD_EXTRACTOR = new HashMap<>();
    static {
        FIELD_EXTRACTOR.put("fullNo", c -> n(c.getFullNo()));
        FIELD_EXTRACTOR.put("name", c -> n(c.getName()));
        FIELD_EXTRACTOR.put("workNo", c -> n(c.getWorkNo()));
        FIELD_EXTRACTOR.put("startDate", c -> c.getStartDate() == null ? "" : DATE_FMT.format(c.getStartDate()));
        FIELD_EXTRACTOR.put("workDays", c -> c.getDaysToTarget() == null ? "" : String.valueOf(c.getDaysToTarget()));
        FIELD_EXTRACTOR.put("wishes", c -> n(c.getWishes()));
        FIELD_EXTRACTOR.put("createdAt", c -> c.getCreatedAt() == null ? "" : DATETIME_FMT.format(c.getCreatedAt()));
    }

    private static String n(String s){ return s == null ? "" : s; }

    public byte[] exportCsv(List<String> columns,
                            Integer limit,
                            String q,
                            String fromDate,
                            String toDate,
                            List<String> ids) {
        List<String> cols = normalizeColumns(columns);
        // 上限保护
        final int pageSize = Math.max(1, Math.min(limit == null ? 1000 : limit, 5000));

        List<AnnivCertificate> list;

        // ① 优先：前端勾选的行（ids）
        if (ids != null && !ids.isEmpty()) {
            // 这里假设 ids 是 “证书编号 fullNo”；如果你传的是主键 id，下面这一句换成 findByIdIn(ids)
            list = certRepo.findByFullNoIn(ids);

            // 按前端勾选顺序排序
            Map<String, Integer> order = new HashMap<>();
            for (int i = 0; i < ids.size(); i++) order.put(ids.get(i), i);
            list.sort(Comparator.comparingInt(c -> order.getOrDefault(c.getFullNo(), Integer.MAX_VALUE)));

            // 仍然尊重 limit
        } else {
            // ② 普通导出：q / 日期范围 / limit
            list = certRepo.findAllByOrderByCreatedAtDesc(PageRequest.of(0, pageSize));

            if (q != null && !q.isEmpty()) {
                final String kw = q.trim();
                list = list.stream()
                        .filter(c -> contains(c.getFullNo(), kw)
                                || contains(c.getName(), kw)
                                || contains(c.getWorkNo(), kw))
                        .collect(Collectors.toList());
            }

            if ((fromDate != null && !fromDate.isEmpty()) || (toDate != null && !toDate.isEmpty())) {
                LocalDateTime from = (fromDate == null || fromDate.isEmpty())
                        ? LocalDate.MIN.atStartOfDay()
                        : LocalDate.parse(fromDate).atStartOfDay();

                // 结束时间含当日 23:59:59
                LocalDateTime to = (toDate == null || toDate.isEmpty())
                        ? LocalDate.MAX.atTime(23, 59, 59)
                        : LocalDate.parse(toDate).atTime(23, 59, 59);

                list = list.stream()
                        .filter(c -> {
                            LocalDateTime t = c.getCreatedAt();
                            return t != null && !t.isBefore(from) && !t.isAfter(to);
                        })
                        .collect(Collectors.toList());
            }

            // 再截一次，确保不超过 limit
        }
        if (list.size() > pageSize) {
            list = list.subList(0, pageSize);
        }

        // —— 生成 CSV（含 UTF-8 BOM，Excel 友好）
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // BOM
            out.write(0xEF); out.write(0xBB); out.write(0xBF);

            // 表头
            String header = cols.stream()
                    .map(HEADER_MAP::get)  // 你已有的“列名中文”映射
                    .collect(Collectors.joining(","));
            out.write((header + "\n").getBytes(StandardCharsets.UTF_8));

            // 数据
            for (AnnivCertificate c : list) {
                String line = cols.stream()
                        .map(k -> csvEscape(FIELD_EXTRACTOR
                                .getOrDefault(k, cc -> "")
                                .apply(c)))            // 你已有的“取值函数”映射
                        .collect(Collectors.joining(","));
                out.write((line + "\n").getBytes(StandardCharsets.UTF_8));
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }


    private static boolean contains(String src, String kw){ return src != null && src.contains(kw); }

    private static List<String> normalizeColumns(List<String> input) {
        List<String> all = new ArrayList<>(HEADER_MAP.keySet());
        if (input == null || input.isEmpty()) return Arrays.asList("fullNo","name","workNo","startDate","workDays","wishes","createdAt","status");
        // 只保留合法列，并保持前端勾选顺序
        return input.stream().filter(all::contains).collect(Collectors.toList());
    }

    // RFC4180 兼容：包含逗号/引号/换行需要用双引号包起来，引号要转义为两连引号
    private static String csvEscape(String v) {
        if (v == null) return "";
        boolean needsQuote = v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r");
        String s = v.replace("\"", "\"\"");
        return needsQuote ? "\"" + s + "\"" : s;
    }

}
