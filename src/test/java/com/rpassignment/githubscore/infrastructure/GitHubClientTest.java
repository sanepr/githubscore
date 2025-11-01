package com.rpassignment.githubscore.infrastructure;


import com.rpassignment.githubscore.infrastructure.github.GitHubClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
@EnableRetry
class GitHubClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GitHubClient gitHubClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gitHubClient = new GitHubClient(
                restTemplate,
                "https://api.github.com/search/repositories",
                "2022-11-28",
                "dummy-token"
        );
    }

    @Test
    void testSearchRepositoriesSuccessfulResponse() {
        Map<String, Object> mockResponse = Map.of("total_count", 123);
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()
        )).thenReturn(response);

        Map<String, Object> result = gitHubClient
                .searchRepositories("language:java", "stars",
                        "desc", 1, 10, false);

        assertThat(result).containsKey("total_count");
        verify(restTemplate, times(1))
                .exchange(anyString(), any(), any(), ArgumentMatchers
                        .<ParameterizedTypeReference<Map<String, Object>>>any());
    }

    @Test
    void testSearchRepositoriesClientErrorThrowsException() {
        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        assertThatThrownBy(() ->
                gitHubClient.searchRepositories("language:java", "stars",
                        "desc", 1, 10, false)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("GitHub API");
    }

    @Test
    void testSearchRepositoriesServerErrorRetriesAndThrowsException() {
        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()
        )).thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE));

        assertThatThrownBy(() ->
                gitHubClient.searchRepositories("language:java", "stars",
                        "desc", 1, 10, false)
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testSearchRepositoriesInvalidParametersThrowsException() {
        assertThatThrownBy(() ->
                gitHubClient.searchRepositories("", "stars", "desc",
                        1, 10, false)
        ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Bad request to GitHub API (400)");

        assertThatThrownBy(() ->
                gitHubClient.searchRepositories("language:java", "stars",
                        "desc", 1, 200, false)
        ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Bad request to GitHub API (400)");

        assertThatThrownBy(() ->
                gitHubClient.searchRepositories("language:java", "stars",
                        "desc", 0, 10, false)
        ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Bad request to GitHub API (400)");
    }

    @Test
    void testRecoverReturnsErrorMap() {
        RuntimeException ex = new RuntimeException("Simulated failure");

        Map<String, Object> result = gitHubClient.recover(ex);

        assertThat(result).containsKey("error");
    }
}
