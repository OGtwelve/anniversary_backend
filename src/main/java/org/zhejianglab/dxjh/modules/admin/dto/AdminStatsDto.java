package org.zhejianglab.dxjh.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@Data
@AllArgsConstructor
public class AdminStatsDto {
    long totalCertificates;
    long todaySubmissions;
    double averageWorkYears; // 小数 1 位
    long validBlessings;
}
