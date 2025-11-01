package com.rpassignment.githubscore.integration;

import com.rpassignment.githubscore.infrastructure.github.GitHubClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubScoreApplicationTestsIT {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private GitHubClient gitHubClient;

	@Autowired
	private RestTemplate clientRestTemplate;

	private MockRestServiceServer mockServer;

    @BeforeEach
	void setup() {
		mockServer = MockRestServiceServer.createServer(clientRestTemplate);
	}

	@Test
	void testGetRepositoriesApiResponse() {
        String githubApiResponse = """
                {
                  "total_count": 1,
                  "incomplete_results": false,
                  "items": [
                    {
                      "name": "java-repo",
                      "owner": {"login": "owner1"},
                      "stargazers_count": 50,
                      "forks_count": 20,
                      "updated_at": "2025-01-01T10:00:00Z"
                    }
                  ]
                }
                """;
        mockServer.expect(requestTo("https://api.github.com/search/repositories?q=java&per_page=30&page=1"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(githubApiResponse, MediaType.APPLICATION_JSON));

		ResponseEntity<String> response = restTemplate.getForEntity(
				"/api/repositories?language=java&earliestCreatedDate=2023-01-01",
				String.class
		);

		assertEquals(200, response.getStatusCodeValue());

		String body = response.getBody();
		assert body != null;
		assert body.contains("java-repo");
		assert body.contains("owner1");
		assert body.contains("50");
		assert body.contains("20");

		mockServer.verify();
	}
}
