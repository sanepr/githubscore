package com.rpassignment.githubscore.domain.service;

import com.rpassignment.githubscore.domain.model.Repository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoringAlgorithmTest {

    private static final double DELTA = 0.01;

    static Stream<TestCase> repositoryTestCases() {
        return Stream.of(
                new TestCase("RecentRepo", "owner1", 50, 20, LocalDate.now(), 25.0 + 6.0 + 20.0),
                new TestCase("OldRepo", "owner2", 100, 50, LocalDate.now().minusYears(2), 50.0 + 15.0 + 0.0),
                new TestCase("ZeroRepo", "owner3", 0, 0, LocalDate.now(), 0.0 + 0.0 + 20.0),
                new TestCase("FutureRepo", "owner4", 10, 5, LocalDate.now().plusDays(10), 5.0 + 1.5 + 20.547945)
        );
    }

    @ParameterizedTest
    @MethodSource("repositoryTestCases")
    void testCalculatePopularity(TestCase testCase) {
        Repository repo = new Repository(
                testCase.name,
                testCase.owner,
                testCase.stars,
                testCase.forks,
                testCase.lastUpdated
        );

        double actualPopularity = ScoringAlgorithm.calculatePopularity(repo);

        assertEquals(testCase.expectedPopularity, actualPopularity, DELTA);
    }

    private static class TestCase {
        String name;
        String owner;
        int stars;
        int forks;
        LocalDate lastUpdated;
        double expectedPopularity;

        public TestCase(
                String name,
                String owner,
                int stars,
                int forks,
                LocalDate lastUpdated,
                double expectedPopularity
        ) {
            this.name = name;
            this.owner = owner;
            this.stars = stars;
            this.forks = forks;
            this.lastUpdated = lastUpdated;
            this.expectedPopularity = expectedPopularity;
        }
    }
}
