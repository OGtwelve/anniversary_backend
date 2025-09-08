package org.zhejianglab.dxjh.modules.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@Data
public class AdminExportRequest {
    /** 需要导出的列 key（顺序即导出顺序）：fullNo,name,workNo,startDate,workDays,wishes,createdAt,status */
    private List<String> columns;

    /** 勾选导出的证书编号（前端“选择导出”时传），如不传则走 q/时间/limit 过滤 */
    private List<String> ids;

    /** 可选：导出条数上限（默认 1000，上限 5000） */
    private Integer limit;

    /** 可选：简单搜索（证书号/姓名/工号包含） */
    private String q;

    /** 可选：时间范围（创建时间），yyyy-MM-dd */
    private String fromDate;
    private String toDate;

    /** 可选：csv/xlsx，默认 csv */
    private String format;
}