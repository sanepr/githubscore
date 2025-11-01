package com.rpassignment.githubscore.infrastructure.github.exception;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;


public class GitHubExceptionMapper {

    private static final Map<HttpStatus, ExceptionDetails> EXCEPTION_MAP = new HashMap<>();

    static {
        EXCEPTION_MAP.put(HttpStatus.UNPROCESSABLE_ENTITY,
                new ExceptionDetails(
                        "Validation failed or endpoint spammed (422)",
                        HttpStatus.UNPROCESSABLE_ENTITY
                )
        );

        EXCEPTION_MAP.put(HttpStatus.NOT_MODIFIED,
                new ExceptionDetails(
                        "Not modified (304)",
                        HttpStatus.NOT_MODIFIED
                )
        );

        EXCEPTION_MAP.put(HttpStatus.BAD_REQUEST,
                new ExceptionDetails(
                        "Bad request to GitHub API (400)",
                        HttpStatus.BAD_REQUEST
                )
        );

        EXCEPTION_MAP.put(HttpStatus.FORBIDDEN,
                new ExceptionDetails(
                        "Access forbidden or rate limit exceeded (403)",
                        HttpStatus.FORBIDDEN
                )
        );

        EXCEPTION_MAP.put(HttpStatus.NOT_FOUND,
                new ExceptionDetails("Requested repository not found (404)",
                        HttpStatus.NOT_FOUND
                )
        );

        EXCEPTION_MAP.put(HttpStatus.SERVICE_UNAVAILABLE,
                new ExceptionDetails(
                        "GitHub API service unavailable (503)",
                        HttpStatus.SERVICE_UNAVAILABLE
                )
        );

        EXCEPTION_MAP.put(HttpStatus.INTERNAL_SERVER_ERROR,
                new ExceptionDetails(
                        "GitHub internal error (500)",
                        HttpStatus.INTERNAL_SERVER_ERROR
                )
        );
    }


    public static GitHubSearchException mapException(Throwable ex, HttpStatus status, String details) {
        ExceptionDetails mapped = EXCEPTION_MAP.get(status);

        if (mapped != null) {
            return new GitHubSearchException(mapped.message(), mapped.status());
        }

        String message = String.format("Unexpected error: %s - %s", status, details);
        return new GitHubSearchException(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private record ExceptionDetails(String message, HttpStatus status) {}
}
