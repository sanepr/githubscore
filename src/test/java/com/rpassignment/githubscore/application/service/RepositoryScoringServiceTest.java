package com.rpassignment.githubscore.application.service;

import com.rpassignment.githubscore.application.dto.RepositoryRequest;
import com.rpassignment.githubscore.application.dto.RepositoryResponse;
import com.rpassignment.githubscore.domain.model.Repository;
import com.rpassignment.githubscore.domain.service.ScoringAlgorithm;
import com.rpassignment.githubscore.infrastructure.github.GitHubClient;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;

class RepositoryScoringServiceTest {

    private static Stream<TestCase> repositoryTestCases() {
        return Stream.of(
                new TestCase(8247368L, "Repo1", "owner1", 50, 20, "2025-01-01T10:00:00Z"),
                new TestCase(8247369L, "Repo2", "owner2", 100, 50, "2024-06-01T12:30:00Z")
        );
    }

    @ParameterizedTest
    @MethodSource("repositoryTestCases")
    void testGetRepositories(TestCase testCase) {
        GitHubClient mockClient = Mockito.mock(GitHubClient.class);

        Map<String, Object> mockResponse = Map.of(
                "items", List.of(
                        Map.of(
                                    "id", testCase.id,
                                "name", testCase.name,
                                "owner", Map.of("login", testCase.owner),
                                "stargazers_count", testCase.stars,
                                "forks_count", testCase.forks,
                                "updated_at", testCase.updatedAt
                        )
                )
        );

        Mockito.when(mockClient.searchRepositories(
                any(), any(), any(), any(), any(), anyInt(), anyInt(), anyBoolean()
        )).thenReturn(mockResponse);
        RepositoryScoringService service = new RepositoryScoringService(mockClient);

        RepositoryRequest request = RepositoryRequest.builder()
                .query("spring")
                .language("java")
                .page(1)
                .size(30)
                .sort("stars")
                .order("desc")
                .build();


        List<RepositoryResponse> responses = service.getRepositories(request);

        assertEquals(1, responses.size());

        RepositoryResponse repoResponse = responses.get(0);

        assertEquals(testCase.name, repoResponse.getName());
        assertEquals(testCase.owner, repoResponse.getOwner());
        assertEquals(testCase.stars, repoResponse.getStars());
        assertEquals(testCase.forks, repoResponse.getForks());

        Repository repo = new Repository(
                testCase.id,
                testCase.name,
                testCase.owner,
                testCase.stars,
                testCase.forks,
                ZonedDateTime.parse(testCase.updatedAt).toLocalDate()
        );
        double expectedPopularity = ScoringAlgorithm.calculatePopularity(repo);
        assertEquals(expectedPopularity, repoResponse.getPopularityScore(), 0.01);
    }

    private static class TestCase {
        Long id;
        String name;
        String owner;
        int stars;
        int forks;
        String updatedAt;

        public TestCase(Long id, String name, String owner, int stars, int forks, String updatedAt) {
            this.id = id;
            this.name = name;
            this.owner = owner;
            this.stars = stars;
            this.forks = forks;
            this.updatedAt = updatedAt;
        }
    }
}
