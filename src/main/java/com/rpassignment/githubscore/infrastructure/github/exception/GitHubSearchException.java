package com.rpassignment.githubscore.infrastructure.github.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GitHubSearchException extends RuntimeException {

    private final HttpStatus status;

    public GitHubSearchException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public GitHubSearchException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
