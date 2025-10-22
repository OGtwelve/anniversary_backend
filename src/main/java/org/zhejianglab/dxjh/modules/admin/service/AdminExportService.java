package org.zhejianglab.dxjh.modules.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivCertificate;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivCertificate_OpenDay;
import org.zhejianglab.dxjh.modules.annivcert.repository.AnnivCertificateRepository;
import org.zhejianglab.dxjh.modules.annivcert.repository.AnnivCertificate_OpenDayRepository;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminExportService {

    public enum CertExportType { ANNIV, OPEN_DAY }

    private final AnnivCertificateRepository certRepo;
    private final AnnivCertificate_OpenDayRepository certOpenDayRepo;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy/M/d");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss");

    // ——— 周年版：列头
    private static final Map<String, String> HEADER_ANNIV = new LinkedHashMap<>();
    static {
        HEADER_ANNIV.put("fullNo",   "证书编号");
        HEADER_ANNIV.put("name",     "姓名");
        HEADER_ANNIV.put("workNo",   "工号");
        HEADER_ANNIV.put("startDate","入职时间");
        HEADER_ANNIV.put("workDays", "工龄(天)");
        HEADER_ANNIV.put("wishes",   "祝福语");
        HEADER_ANNIV.put("createdAt","首次注册时间");
    }
    private static final List<String> DEFAULT_COLS_ANNIV =
            Arrays.asList("fullNo","name","workNo","startDate","workDays","wishes","createdAt");

    // ——— 开放日：列头（注意不要误写到 HEADER_ANNIV）
    private static final Map<String, String> HEADER_OPEN = new LinkedHashMap<>();
    static {
        HEADER_OPEN.put("fullNo",   "证书编号");
        HEADER_OPEN.put("name",     "姓名");
        HEADER_OPEN.put("dateOfBirth","出生日期");
        HEADER_OPEN.put("wishes",   "祝福语");
        HEADER_OPEN.put("createdAt","首次注册时间");
    }
    private static final List<String> DEFAULT_COLS_OPEN =
            Arrays.asList("fullNo","name","dateOfBirth","wishes","createdAt");

    // ——— 周年版：字段提取
    private static final Map<String, Function<AnnivCertificate, String>> FX_ANNIV = new HashMap<>();
    static {
        FX_ANNIV.put("fullNo",    c -> n(c.getFullNo()));
        FX_ANNIV.put("name",      c -> n(c.getName()));
        FX_ANNIV.put("workNo",    c -> n(c.getWorkNo()));
        FX_ANNIV.put("startDate", c -> c.getStartDate()==null? "" : DATE_FMT.format(c.getStartDate()));
        FX_ANNIV.put("workDays",  c -> c.getDaysToTarget()==null? "" : String.valueOf(c.getDaysToTarget()));
        FX_ANNIV.put("wishes",    c -> n(c.getWishes()));
        FX_ANNIV.put("createdAt", c -> c.getCreatedAt()==null? "" : DATETIME_FMT.format(c.getCreatedAt()));
    }

    // ——— 开放日：字段提取（注意字段名差异：dateOfBirth）
    private static final Map<String, Function<AnnivCertificate_OpenDay, String>> FX_OPEN = new HashMap<>();
    static {
        FX_OPEN.put("fullNo",      c -> n(c.getFullNo()));
        FX_OPEN.put("name",        c -> n(c.getName()));
        FX_OPEN.put("dateOfBirth", c -> c.getDateOfBirth()==null? "" : DATE_FMT.format(c.getDateOfBirth()));
        FX_OPEN.put("wishes",      c -> n(c.getWishes()));
        FX_OPEN.put("createdAt",   c -> c.getCreatedAt()==null? "" : DATETIME_FMT.format(c.getCreatedAt()));
    }

    private static String n(String s){ return s == null ? "" : s; }
    private static boolean contains(String src, String kw){ return src != null && src.contains(kw); }

    // ===== 对外主接口（用枚举区分类型）=====
    public byte[] exportCsvHelper(List<String> columns,
                                  Integer limit,
                                  String q,
                                  String fromDate,
                                  String toDate,
                                  List<String> ids,
                                  CertExportType type) {

        final int pageSize = Math.max(1, Math.min(limit == null ? 1000 : limit, 5000));
        final LocalDateTime from = parseFrom(fromDate);
        final LocalDateTime to   = parseTo(toDate);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // BOM
            out.write(0xEF); out.write(0xBB); out.write(0xBF);

            if (type == CertExportType.ANNIV) {
                // 1) 列 / 表头
                List<String> cols = normalizeColumns(columns, HEADER_ANNIV.keySet(), DEFAULT_COLS_ANNIV);
                writeHeader(out, cols, HEADER_ANNIV);

                // 2) 数据
                List<AnnivCertificate> list = fetchAnniv(ids, q, from, to, pageSize);
                for (AnnivCertificate c : list) {
                    String line = cols.stream()
                            .map(k -> csvEscape(FX_ANNIV.getOrDefault(k, cc -> "").apply(c)))
                            .collect(Collectors.joining(","));
                    out.write((line + "\n").getBytes(StandardCharsets.UTF_8));
                }
            } else { // OPEN_DAY
                List<String> cols = normalizeColumns(columns, HEADER_OPEN.keySet(), DEFAULT_COLS_OPEN);
                writeHeader(out, cols, HEADER_OPEN);

                List<AnnivCertificate_OpenDay> list = fetchOpen(ids, q, from, to, pageSize);
                for (AnnivCertificate_OpenDay c : list) {
                    String line = cols.stream()
                            .map(k -> csvEscape(FX_OPEN.getOrDefault(k, cc -> "").apply(c)))
                            .collect(Collectors.joining(","));
                    out.write((line + "\n").getBytes(StandardCharsets.UTF_8));
                }
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }

    // ===== 兼容老签名（Class<?> 参数）=====
    public byte[] exportCsv(List<String> columns,
                                  Integer limit,
                                  String q,
                                  String fromDate,
                                  String toDate,
                                  List<String> ids,
                                  Class<?> entityClass) {
        CertExportType type = (AnnivCertificate_OpenDay.class.equals(entityClass))
                ? CertExportType.OPEN_DAY : CertExportType.ANNIV;
        return exportCsvHelper(columns, limit, q, fromDate, toDate, ids, type);
    }

    // ===== 数据获取 =====
    private List<AnnivCertificate> fetchAnniv(List<String> ids, String q,
                                              LocalDateTime from, LocalDateTime to, int pageSize) {
        List<AnnivCertificate> list;
        if (ids != null && !ids.isEmpty()) {
            list = certRepo.findByFullNoIn(ids);
            // 保持勾选顺序
            Map<String,Integer> order = indexOf(ids);
            list.sort(Comparator.comparingInt(c -> order.getOrDefault(c.getFullNo(), Integer.MAX_VALUE)));
        } else {
            // 你可替换为自己的仓库方法：findAllByOrderByCreatedAtDesc(Pageable)
            list = certRepo.findAllByOrderByCreatedAtDesc(PageRequest.of(0, pageSize));
            if (q != null && !q.isEmpty()) {
                String kw = q.trim();
                list = list.stream().filter(c ->
                        contains(c.getFullNo(), kw) ||
                                contains(c.getName(), kw)   ||
                                contains(c.getWorkNo(), kw)
                ).collect(Collectors.toList());
            }
            list = filterByCreatedAt(list, from, to);
            if (list.size() > pageSize) list = list.subList(0, pageSize);
        }
        return list;
    }

    private List<AnnivCertificate_OpenDay> fetchOpen(List<String> ids, String q,
                                                     LocalDateTime from, LocalDateTime to, int pageSize) {
        List<AnnivCertificate_OpenDay> list;
        if (ids != null && !ids.isEmpty()) {
            list = certOpenDayRepo.findByFullNoIn(ids);
            Map<String,Integer> order = indexOf(ids);
            list.sort(Comparator.comparingInt(c -> order.getOrDefault(c.getFullNo(), Integer.MAX_VALUE)));
        } else {
            list = certOpenDayRepo.findAllByOrderByCreatedAtDesc(PageRequest.of(0, pageSize));
            if (q != null && !q.isEmpty()) {
                String kw = q.trim();
                list = list.stream().filter(c ->
                        contains(c.getFullNo(), kw) ||
                                contains(c.getName(), kw)   ||
                                // 也可允许用生日字符串检索
                                contains(c.getDateOfBirth()==null? null : DATE_FMT.format(c.getDateOfBirth()), kw)
                ).collect(Collectors.toList());
            }
            list = filterByCreatedAt(list, from, to);
            if (list.size() > pageSize) list = list.subList(0, pageSize);
        }
        return list;
    }

    // ===== 工具 =====
    private static Map<String,Integer> indexOf(List<String> ids){
        Map<String,Integer> m = new HashMap<>();
        for (int i=0;i<ids.size();i++) m.put(ids.get(i), i);
        return m;
    }

    private static <T extends Object> List<T> filterByCreatedAt(List<T> src, LocalDateTime from, LocalDateTime to) {
        return src.stream().filter(o -> {
            LocalDateTime t;
            if (o instanceof AnnivCertificate) {
                t = ((AnnivCertificate) o).getCreatedAt();
            } else if (o instanceof AnnivCertificate_OpenDay) {
                t = ((AnnivCertificate_OpenDay) o).getCreatedAt();
            } else return false;
            return t != null && !t.isBefore(from) && !t.isAfter(to);
        }).collect(Collectors.toList());
    }

    private static List<String> normalizeColumns(List<String> input, Set<String> allowed, List<String> defaults) {
        if (input == null || input.isEmpty()) return defaults;
        // 只保留合法列，并保持前端勾选顺序
        return input.stream().filter(allowed::contains).collect(Collectors.toList());
    }

    private static void writeHeader(ByteArrayOutputStream out, List<String> cols, Map<String,String> headerMap)
            throws Exception {
        String header = cols.stream().map(k -> headerMap.getOrDefault(k, k)).collect(Collectors.joining(","));
        out.write((header + "\n").getBytes(StandardCharsets.UTF_8));
    }

    private static String csvEscape(String v) {
        if (v == null) return "";
        boolean needsQuote = v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r");
        String s = v.replace("\"", "\"\"");
        return needsQuote ? "\"" + s + "\"" : s;
    }

    private static LocalDateTime parseFrom(String s) {
        if (s == null || s.isEmpty()) return LocalDate.MIN.atStartOfDay();
        return LocalDate.parse(s).atStartOfDay();
    }
    private static LocalDateTime parseTo(String s) {
        if (s == null || s.isEmpty()) return LocalDate.MAX.atTime(23,59,59);
        return LocalDate.parse(s).atTime(23,59,59);
    }
}
