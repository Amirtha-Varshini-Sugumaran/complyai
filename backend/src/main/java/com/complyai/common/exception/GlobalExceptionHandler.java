package com.complyai.common.exception;

import com.complyai.common.dto.ApiErrorResponse;
import com.complyai.common.dto.FieldValidationError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception,
                                                             HttpServletRequest request) {
        List<FieldValidationError> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(this::mapFieldError)
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", request.getRequestURI(), fieldErrors);
    }

    @ExceptionHandler({
            ResourceNotFoundException.class,
            ConstraintViolationException.class,
            BusinessRuleViolationException.class,
            AccessDeniedDomainException.class
    })
    public ResponseEntity<ApiErrorResponse> handleDomain(RuntimeException exception, HttpServletRequest request) {
        HttpStatus status = switch (exception) {
            case ResourceNotFoundException ignored -> HttpStatus.NOT_FOUND;
            case AccessDeniedDomainException ignored -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.BAD_REQUEST;
        };
        return build(status, status.name(), exception.getMessage(), request.getRequestURI(), List.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException exception, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "Access is denied", request.getRequestURI(), List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception exception, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected server error", request.getRequestURI(), List.of());
    }

    private FieldValidationError mapFieldError(FieldError error) {
        return new FieldValidationError(error.getField(), error.getDefaultMessage());
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status,
                                                   String code,
                                                   String message,
                                                   String path,
                                                   List<FieldValidationError> fieldErrors) {
        return ResponseEntity.status(status).body(
                new ApiErrorResponse(Instant.now(), status.value(), code, message, path, fieldErrors)
        );
    }
}
