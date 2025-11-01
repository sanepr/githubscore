package com.rpassignment.githubscore.infrastructure.github;


import com.rpassignment.githubscore.infrastructure.github.exception.GitHubExceptionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class GitHubClient {

    private final RestTemplate restTemplate;
    private final String githubApiUrl;
    private final String apiVersion;
    private final String githubToken;

    public GitHubClient(
            RestTemplate restTemplate,
            @Value("${github.api.url}") String githubApiUrl,
            @Value("${github.api.version:2022-11-28}") String apiVersion,
            @Value("${github.api.token:}") String githubToken
    ) {
        this.restTemplate = restTemplate;
        this.githubApiUrl = githubApiUrl;
        this.apiVersion = apiVersion;
        this.githubToken = githubToken;
    }

    @Retryable(
            retryFor = {HttpServerErrorException.class, RuntimeException.class},
            maxAttemptsExpression = "#{${github.retry.max-attempts}}",
            backoff = @Backoff(
                    delayExpression = "#{${github.retry.delay}}",
                    multiplierExpression = "#{${github.retry.multiplier}}"
            )
    )
    public Map<String, Object> searchRepositories(
            String query,
            String language,
            String earliestCreatedDate,
            String sort,
            String order,
            int page,
            int size,
            boolean textMatch
    ) {
        validateParameters(query, size, page);

        String url = buildSearchUrl(query, language, earliestCreatedDate, sort, order, size, page);
        HttpHeaders headers = buildHeaders(textMatch);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {
                    }
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }

            throw GitHubExceptionMapper.mapException(
                    new RuntimeException("Unexpected response from GitHub API."),
                    HttpStatus.valueOf(response.getStatusCode().value()),
                    String.valueOf(response.getBody())
            );

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw GitHubExceptionMapper.mapException(
                    e,
                    HttpStatus.valueOf(e.getStatusCode().value()),
                    e.getResponseBodyAsString()
            );
        }
    }

    @Recover
    public Map<String, Object> recover(RuntimeException e) {
        log.error("Retries exhausted.", e);
        return Map.of("error", "Generic failure");
    }

    private HttpHeaders buildHeaders(boolean textMatch) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", textMatch ? "application/vnd.github.text-match+json" : "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", apiVersion);

        if (githubToken != null && !githubToken.isBlank()) {
            headers.set("Authorization", "Bearer " + githubToken);
        }

        return headers;
    }

    private String buildSearchUrl(
            String query,
            String language,
            String earliestCreatedDate,
            String sort,
            String order,
            int perPage,
            int page
    ) {
        StringBuilder qBuilder = new StringBuilder();

        if (query != null && !query.isBlank()) {
            qBuilder.append(query.trim());
        }

        if (language != null && !language.isBlank()) {
            qBuilder.append("+language:").append(language.trim());
        }

        if (earliestCreatedDate != null && !earliestCreatedDate.isBlank()) {
            qBuilder.append("+created:<").append(earliestCreatedDate.trim());
        }

        // Construct final URI with encoded=false to avoid double encoding
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(githubApiUrl)
                .queryParam("q", qBuilder.toString())
                .queryParam("per_page", perPage)
                .queryParam("page", page);

        Optional.ofNullable(sort)
                .filter(s -> !s.isBlank())
                .ifPresent(s -> builder.queryParam("sort", s));

        Optional.ofNullable(order)
                .filter(o -> !o.isBlank())
                .ifPresent(o -> builder.queryParam("order", o));

        // Build and encode only URI path, not query param
        return builder.build(false).toUriString();  // <--- THIS is the key change
    }


    private void validateParameters(String query, int perPage, int page) {
        if (query == null || query.isBlank()) {
            throw GitHubExceptionMapper.mapException(
                    new IllegalArgumentException("Query parameter cannot be empty."),
                    HttpStatus.BAD_REQUEST,
                    "Missing required parameter 'q'"
            );
        }

        if (perPage < 1 || perPage > 100) {
            throw GitHubExceptionMapper.mapException(
                    new IllegalArgumentException("per_page must be between 1 and 100."),
                    HttpStatus.BAD_REQUEST,
                    "Invalid per_page value"
            );
        }

        if (page < 1) {
            throw GitHubExceptionMapper.mapException(
                    new IllegalArgumentException("page must be 1 or greater."),
                    HttpStatus.BAD_REQUEST,
                    "Invalid page value"
            );
        }
    }
}
