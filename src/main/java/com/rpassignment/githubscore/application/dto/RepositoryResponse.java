package com.rpassignment.githubscore.application.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RepositoryResponse {
    private String name;
    private String owner;
    private int stars;
    private int forks;
    private String lastUpdated;
    private double popularityScore;
}

