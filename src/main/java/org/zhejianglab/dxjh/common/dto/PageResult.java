package org.zhejianglab.dxjh.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    List<T> items;
    long total;
    int page;
    int size;
}