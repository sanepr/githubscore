package com.rpassignment.githubscore.application.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Getter
public class RepositoryRequest {
    private String language;
    private String earliestCreatedDate;
    private Integer page;
    private Integer size;
    private String sort;
    private String order;
    private boolean textMatch;
}
