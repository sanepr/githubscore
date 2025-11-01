package com.rpassignment.githubscore.domain.service;


import com.rpassignment.githubscore.domain.model.Repository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ScoringAlgorithm {

    public static double calculatePopularity(Repository repo) {
        double starsWeight = 0.5;
        double forksWeight = 0.3;
        double recencyWeight = 0.2;

        double starsScore = repo.getStars() * starsWeight;
        double forksScore = repo.getForks() * forksWeight;

        long daysSinceUpdate = ChronoUnit.DAYS.between(repo.getLastUpdated(), LocalDate.now());
        double recencyScore = Math.max(0, 1 - (daysSinceUpdate / 365.0)) * 100 * recencyWeight;

        return starsScore + forksScore + recencyScore;
    }
}
