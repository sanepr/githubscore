package com.rpassignment.githubscore.mapper;

import com.rpassignment.githubscore.application.dto.RepositoryResponse;
import com.rpassignment.githubscore.domain.model.Repository;
import com.rpassignment.githubscore.shared.mapper.RepositoryMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryMapperTest {

    @Test
    void testRequestMapper() {
        // given
        LocalDate updated = LocalDate.of(2025, 10, 1);
        Repository repo = new Repository("repo-name", "owner-login", 123, 45, updated);
        repo.setPopularityScore(99.5);

        // when
        RepositoryResponse dto = RepositoryMapper.toDto(repo);

        // then
        assertNotNull(dto);
        assertEquals("repo-name", dto.getName());
        assertEquals("owner-login", dto.getOwner());
        assertEquals(123, dto.getStars());
        assertEquals(45, dto.getForks());
        assertEquals(updated.toString(), dto.getLastUpdated());
        assertEquals(99.5, dto.getPopularityScore(), 1e-9);
    }
}

