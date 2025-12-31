package com.seyitkarahan.akilli_ajanda_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, "User Already Exists");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "User Not Found");
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryNotFound(CategoryNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "Category Not Found");
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedAction(UnauthorizedActionException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, "Unauthorized Action");
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotificationNotFound(NotificationNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "Notification Not Found");
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTaskNotFound(TaskNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "Task Not Found");
    }

    @ExceptionHandler(RecurringTaskRuleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRecurringTaskRuleNotFound(RecurringTaskRuleNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "Recurring Task Rule Not Found");
    }

    @ExceptionHandler(UserSettingsNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserSettingsNotFound(UserSettingsNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "User Settings Not Found");
    }

    @ExceptionHandler(UserSettingsAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserSettingsAlreadyExists(UserSettingsAlreadyExistsException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, "User Settings Already Exists");
    }

    @ExceptionHandler()
    public ResponseEntity<Map<String, Object>> handleEventNotFound(EventNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "Event Not Found");
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleInvalidCategory(InvalidCategoryException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, "Invalid Category");
    }

    // General Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex, HttpServletRequest request) throws Exception {
        if(request.getRequestURI().startsWith("/v3/api-docs") || request.getRequestURI().startsWith("/swagger-ui")) {
            throw ex;
        }
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "status", 401,
                        "error", "Unauthorized",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status, String error) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

}
