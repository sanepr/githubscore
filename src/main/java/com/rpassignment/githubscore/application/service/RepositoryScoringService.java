package com.rpassignment.githubscore.application.service;


import com.rpassignment.githubscore.application.dto.RepositoryRequest;
import com.rpassignment.githubscore.application.dto.RepositoryResponse;
import com.rpassignment.githubscore.domain.model.Repository;
import com.rpassignment.githubscore.domain.service.ScoringAlgorithm;
import com.rpassignment.githubscore.infrastructure.github.GitHubClient;
import com.rpassignment.githubscore.shared.mapper.RepositoryMapper;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RepositoryScoringService {

    private final GitHubClient gitHubClient;

    public RepositoryScoringService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    public List<RepositoryResponse> getRepositories(RepositoryRequest request) {
        Map<String, Object> response = gitHubClient.searchRepositories(
                request.getLanguage(),
                request.getSort(),
                request.getOrder(),
                request.getPage(),
                request.getSize(),
                false
        );

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
        List<RepositoryResponse> results = new ArrayList<>();

        for (Map<String, Object> item : items) {
            Repository repo = new Repository(
                    (String) item.get("name"),
                    (String) ((Map<String, Object>) item.get("owner")).get("login"),
                    (Integer) item.get("stargazers_count"),
                    (Integer) item.get("forks_count"),
                    ZonedDateTime.parse((String) item.get("updated_at")).toLocalDate()
            );
            repo.setPopularityScore(ScoringAlgorithm.calculatePopularity(repo));
            results.add(RepositoryMapper.toDto(repo));
        }

        return results;
    }

}
