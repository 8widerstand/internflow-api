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
    public ResponseEntity<ApiErrorResponse> handleValidationError(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        ApiErrorResponse response = new ApiErrorResponse("Validation failed", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("request", "Request body is malformed or contains invalid values");
        ApiErrorResponse response = new ApiErrorResponse("Invalid request body", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        Map<String, String> errors = new HashMap<>();
        String parameterName = exception.getName();
        if ("status".equals(parameterName)) {
            errors.put(parameterName, "Invalid internship status");
        } else {
            errors.put(parameterName, "Invalid request parameter");
        }
        ApiErrorResponse response = new ApiErrorResponse("Invalid request parameter", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        Map<String, String> errors = new HashMap<>();
        String[] parts = exception.getMessage().split(": ", 2);
        if (parts.length == 2) {
            errors.put(parts[0], parts[1]);
        } else {
            errors.put("request", exception.getMessage());
        }
        ApiErrorResponse response = new ApiErrorResponse("Invalid request parameter", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("resource", exception.getMessage());

        ApiErrorResponse response = new ApiErrorResponse("Resource not found", errors);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceConflictException(
            ResourceConflictException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("resource", exception.getMessage());
        ApiErrorResponse response = new ApiErrorResponse("Resource conflict", errors);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
