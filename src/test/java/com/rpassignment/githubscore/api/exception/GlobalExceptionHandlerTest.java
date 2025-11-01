package com.rpassignment.githubscore.api.exception;


import com.rpassignment.githubscore.infrastructure.github.exception.GitHubSearchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest mockRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        mockRequest = Mockito.mock(WebRequest.class);
        when(mockRequest.getDescription(false)).thenReturn("/test/path");
    }

    @Test
    void testHandleApiException() {
        ApiException apiException = new ApiException("API failed");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleApiException(apiException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("API failed", response.getBody().get("error"));
    }

    @Test
    void testHandleAllExceptions() {
        Exception exception = new Exception("Something went wrong");

        ResponseEntity<Object> response = exceptionHandler.handleAllExceptions(exception, mockRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(500, body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals("Something went wrong", body.get("message"));
        assertEquals("/test/path", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleGitHubSearchException() {
        GitHubSearchException gitHubException = new GitHubSearchException(
                "GitHub API rate limit exceeded",
                HttpStatus.FORBIDDEN
        );

        ResponseEntity<Map<String, Object>> response = exceptionHandler
                .handleGitHubSearchException(gitHubException, mockRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(403, body.get("status"));
        assertEquals("Forbidden", body.get("error"));
        assertEquals("GitHub API rate limit exceeded", body.get("message"));
        assertEquals("/test/path", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }
}
