package org.zhejianglab.anniversary.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessResponse {
    private String message;
    private Object data;
}

