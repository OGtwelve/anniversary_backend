package org.zhejianglab.dxjh.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zhejianglab.dxjh.common.response.ErrorResponse;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // JSON 体校验失败：@Valid @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<List<Map<String,String>>>> handleMethodArgNotValid(MethodArgumentNotValidException ex) {
        List<Map<String,String>> errors = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            Map<String,String> m = new HashMap<>();
            m.put("field", fe.getField());
            m.put("message", fe.getDefaultMessage());
            errors.add(m);
        }
        return ResponseEntity.badRequest()
                .body(new ErrorResponse<>("参数校验失败", errors));
    }

    // 路径/查询参数校验失败：@Validated + @NotBlank 等
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse<String>> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse<>("参数校验失败", ex.getMessage()));
    }

    // 业务异常
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse<Void>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse<>(ex.getMessage(), null));
    }

    // 兜底
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse<Void>> handleOther(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse<>("服务器内部错误", null));
    }
}


