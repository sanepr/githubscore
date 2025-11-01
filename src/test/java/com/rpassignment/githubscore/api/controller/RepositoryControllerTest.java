package com.rpassignment.githubscore.api.controller;

import com.rpassignment.githubscore.application.dto.RepositoryRequest;
import com.rpassignment.githubscore.application.dto.RepositoryResponse;
import com.rpassignment.githubscore.application.service.RepositoryScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RepositoryControllerTest {

    private RepositoryScoringService mockService;
    private RepositoryController controller;

    @BeforeEach
    void setUp() {
        mockService = Mockito.mock(RepositoryScoringService.class);
        controller = new RepositoryController(mockService);
    }

    @Test
    void testGetRepositories() {
        RepositoryResponse mockResponse = RepositoryResponse.builder()
                .name("TestRepo")
                .owner("TestOwner")
                .stars(50)
                .forks(10)
                .popularityScore(75.0)
                .build();

        when(mockService.getRepositories(any(RepositoryRequest.class)))
                .thenReturn(List.of(mockResponse));

        String query = "test";
        String language = "java";
        String earliestCreatedDate = "2023-01-01";
        int page = 1;
        int size = 30;

        List<RepositoryResponse> result = controller.getRepositories(
                query,
                language, earliestCreatedDate, 1, 30
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TestRepo", result.get(0).getName());
        assertEquals("TestOwner", result.get(0).getOwner());
        assertEquals(50, result.get(0).getStars());
        assertEquals(10, result.get(0).getForks());
        assertEquals(75.0, result.get(0).getPopularityScore());

        ArgumentCaptor<RepositoryRequest> captor = ArgumentCaptor.forClass(RepositoryRequest.class);
        verify(mockService, times(1)).getRepositories(captor.capture());
        RepositoryRequest capturedRequest = captor.getValue();
        assertEquals(language, capturedRequest.getLanguage());
        assertEquals(earliestCreatedDate, capturedRequest.getEarliestCreatedDate());
        assertEquals(page, capturedRequest.getPage());
        assertEquals(size, capturedRequest.getSize());
    }

    @Test
    void testGetRepositoriesDefaultValues() {
        RepositoryResponse mockResponse = RepositoryResponse.builder()
                .name("DefaultRepo")
                .owner("DefaultOwner")
                .stars(10)
                .forks(5)
                .popularityScore(20.0)
                .build();

        when(mockService.getRepositories(any(RepositoryRequest.class)))
                .thenReturn(List.of(mockResponse));

        String query = "test";
        String language = "python";
        String earliestCreatedDate = "2022-01-01";

        List<RepositoryResponse> result = controller.getRepositories(
                query, language, earliestCreatedDate, 1, 30);

        assertNotNull(result);
        assertEquals(1, result.size());

        ArgumentCaptor<RepositoryRequest> captor = ArgumentCaptor.forClass(RepositoryRequest.class);
        verify(mockService).getRepositories(captor.capture());
        RepositoryRequest capturedRequest = captor.getValue();
        assertEquals(1, capturedRequest.getPage());   // default
        assertEquals(30, capturedRequest.getSize());  // default
    }
}
