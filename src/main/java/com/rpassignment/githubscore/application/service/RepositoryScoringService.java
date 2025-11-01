package com.rpassignment.githubscore.application.service;


import com.rpassignment.githubscore.application.dto.RepositoryRequest;
import com.rpassignment.githubscore.application.dto.RepositoryResponse;
import com.rpassignment.githubscore.domain.model.Repository;
import com.rpassignment.githubscore.domain.service.ScoringAlgorithm;
import com.rpassignment.githubscore.infrastructure.github.GitHubClient;
import com.rpassignment.githubscore.shared.mapper.RepositoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RepositoryScoringService {

    private final GitHubClient gitHubClient;

    public RepositoryScoringService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    public List<RepositoryResponse> getRepositories(RepositoryRequest request) {
        Map<String, Object> response = gitHubClient.searchRepositories(
                request.getQuery(),
                request.getLanguage(),
                request.getEarliestCreatedDate(),
                request.getSort(),
                request.getOrder(),
                request.getPage(),
                request.getSize(),
                false
        );

        if (response == null) {
            log.warn("GitHub API returned null response for request: {}", request);
            return Collections.emptyList();
        }

        Object itemsObj = response.get("items");
        if (!(itemsObj instanceof List)) {
            log.warn("GitHub API response missing 'items' field or not a list: {}", response);
            return Collections.emptyList();
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) itemsObj;
        if (items.isEmpty()) {
            log.info("No repositories found for request: {}", request);
            return Collections.emptyList();
        }

        List<RepositoryResponse> results = new ArrayList<>();

        for (Map<String, Object> item : items) {
            try {
                Repository repo = new Repository(
                        ((Number) item.get("id")).longValue(),
                        (String) item.get("name"),
                        (String) ((Map<String, Object>) item.get("owner")).get("login"),
                        (Integer) item.getOrDefault("stargazers_count", 0),
                        (Integer) item.getOrDefault("forks_count", 0),
                        ZonedDateTime.parse((String) item.get("updated_at")).toLocalDate()
                );

                repo.setPopularityScore(ScoringAlgorithm.calculatePopularity(repo));
                results.add(RepositoryMapper.toDto(repo));
            } catch (Exception ex) {
                log.error("Failed to parse repository item: {}", item, ex);
            }
        }

        return results;
    }
}
