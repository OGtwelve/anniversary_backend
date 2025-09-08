package org.zhejianglab.dxjh.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@Data
@AllArgsConstructor
public class AdminTrendDto {
    List<String> labels; // ["9月2日", ...]
    List<Long> values;   // [1, 4, ...]
}