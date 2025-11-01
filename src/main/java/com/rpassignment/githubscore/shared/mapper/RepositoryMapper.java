package com.rpassignment.githubscore.shared.mapper;

import com.rpassignment.githubscore.application.dto.RepositoryResponse;
import com.rpassignment.githubscore.domain.model.Repository;

public class RepositoryMapper {

    public static RepositoryResponse toDto(Repository repo) {
        return RepositoryResponse.builder()
                .name(repo.getName())
                .owner(repo.getOwner())
                .stars(repo.getStars())
                .forks(repo.getForks())
                .lastUpdated(repo.getLastUpdated().toString())
                .popularityScore(repo.getPopularityScore())
                .build();
    }
}
