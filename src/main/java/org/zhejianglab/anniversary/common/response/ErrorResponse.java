package org.zhejianglab.anniversary.common.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
@Setter
@Getter
@Data
public class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}

