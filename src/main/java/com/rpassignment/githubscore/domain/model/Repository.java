package com.rpassignment.githubscore.domain.model;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
public class Repository {
    private final String name;
    private final String owner;
    private final int stars;
    private final int forks;
    private final LocalDate lastUpdated;
    @Getter
    @Setter
    private double popularityScore;

    public Repository(String name, String owner, int stars, int forks, LocalDate lastUpdated) {
        this.name = name;
        this.owner = owner;
        this.stars = stars;
        this.forks = forks;
        this.lastUpdated = lastUpdated;
    }
}
