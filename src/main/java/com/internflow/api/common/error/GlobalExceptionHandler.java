package com.internflow.api.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationError(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        ValidationErrorResponse response = new ValidationErrorResponse("Validation failed", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("request", "Request body is malformed or contains invalid values");
        ValidationErrorResponse response = new ValidationErrorResponse("Invalid request body", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        Map<String, String> errors = new HashMap<>();
        String parameterName = exception.getName();
        if ("status".equals(parameterName)) {
            errors.put(parameterName, "Invalid internship status");
        }else {
            errors.put(parameterName, "Invalid request parameter");
        }
        ValidationErrorResponse response = new ValidationErrorResponse("Invalid request parameter", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}