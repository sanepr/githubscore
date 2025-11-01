package com.rpassignment.githubscore.infrastructure.github.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GitHubSearchException extends RuntimeException {

    private final HttpStatus status;

    public GitHubSearchException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
