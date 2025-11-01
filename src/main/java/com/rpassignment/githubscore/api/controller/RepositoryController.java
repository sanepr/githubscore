package com.rpassignment.githubscore.api.controller;

import com.rpassignment.githubscore.application.dto.RepositoryRequest;
import com.rpassignment.githubscore.application.dto.RepositoryResponse;
import com.rpassignment.githubscore.application.service.RepositoryScoringService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/repositories")
@Validated
public class RepositoryController {

    private final RepositoryScoringService scoringService;

    public RepositoryController(RepositoryScoringService scoringService) {
        this.scoringService = scoringService;
    }

    /**
     * Retrieve GitHub repositories with scoring.
     *
     * @param language            Programming language filter (required)
     * @param earliestCreatedDate Earliest creation date filter in yyyy-MM-dd format (required)
     * @param page                Page number (optional, default 1)
     * @param size                Page size (optional, default 30)
     * @return List of scored repositories
     */
    @GetMapping
    public List<RepositoryResponse> getRepositories(
            @RequestParam String query,
            @RequestParam String language,
            @RequestParam(defaultValue = "2025-01-01")
            @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in YYYY-MM-DD format")
            String earliestCreatedDate,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "30") @Min(1) Integer size
    ) {
        RepositoryRequest request = RepositoryRequest.builder()
                .query(query)
                .language(language)
                .earliestCreatedDate(earliestCreatedDate)
                .page(page)
                .size(size)
                .build();

        return scoringService.getRepositories(request);
    }
}
