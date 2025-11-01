package com.rpassignment.githubscore.api.exception;


import com.rpassignment.githubscore.infrastructure.github.exception.GitHubSearchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> handleApiException(ApiException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        // Log full stack trace
        log.error("Unhandled exception processing request: {} {}", request.getDescription(false), ex);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GitHubSearchException.class)
    public ResponseEntity<Map<String, Object>> handleGitHubSearchException(GitHubSearchException ex, WebRequest request) {
        log.warn("GitHubSearchException: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("status", ex.getStatus().value());
        response.put("error", ex.getStatus().getReasonPhrase());
        response.put("message", ex.getMessage());
        response.put("path", request.getDescription(false));

        return new ResponseEntity<>(response, ex.getStatus());
    }
}
