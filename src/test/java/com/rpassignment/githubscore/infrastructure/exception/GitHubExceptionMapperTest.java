package com.rpassignment.githubscore.infrastructure.exception;


import com.rpassignment.githubscore.infrastructure.github.exception.GitHubExceptionMapper;
import com.rpassignment.githubscore.infrastructure.github.exception.GitHubSearchException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GitHubExceptionMapperTest {

    static Stream<TestCase> exceptionMappingProvider() {
        return Stream.of(new TestCase(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Some validation issue",
                "Validation failed or endpoint spammed (422)"),
                new TestCase(
                        HttpStatus.FORBIDDEN,
                        "Rate limit exceeded",
                        "Access forbidden or rate limit exceeded (403)"
                ),
                new TestCase(
                        HttpStatus.BAD_REQUEST,
                        "Missing parameter",
                        "Bad request to GitHub API (400)"),
                new TestCase(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "GitHub API error",
                        "GitHub internal error (500)"
                )
        );
    }

    @ParameterizedTest(name = "status={0}, expectedMessage={2}")
    @MethodSource("exceptionMappingProvider")
    void testKnownStatusMapping(TestCase testCase) {
        GitHubSearchException ex = GitHubExceptionMapper.mapException(
                new RuntimeException("Test exception"), testCase.status, testCase.details);

        assertNotNull(ex);
        assertEquals(testCase.expectedMessage, ex.getMessage());
        assertEquals(testCase.status, ex.getStatus());
    }

    @ParameterizedTest
    @MethodSource("unmappedStatusProvider")
    void testUnmappedStatusFallback(HttpStatus unmappedStatus, String details) {
        GitHubSearchException ex = GitHubExceptionMapper.mapException(
                new RuntimeException("Unknown"), unmappedStatus, details);

        assertNotNull(ex);
        assertTrue(ex.getMessage().contains("Unexpected error"));
        assertTrue(ex.getMessage().contains(details));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
    }

    static Stream<Object[]> unmappedStatusProvider() {
        return Stream.of(new Object[]{HttpStatus.I_AM_A_TEAPOT, "I'm a teapot"},
                new Object[]{HttpStatus.CONFLICT, "Conflict occurred"});
    }

    private record TestCase(HttpStatus status, String details, String expectedMessage) {
    }
}
