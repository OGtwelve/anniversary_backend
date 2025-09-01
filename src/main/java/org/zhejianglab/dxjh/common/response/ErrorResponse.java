package org.zhejianglab.dxjh.common.response;

import lombok.*;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse<T> {
    private String message;
    private T errors;  // 可为字段错误列表/字符串/null
}


