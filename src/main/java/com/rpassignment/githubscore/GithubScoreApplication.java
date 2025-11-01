package com.rpassignment.githubscore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.retry.annotation.EnableRetry;

@Slf4j
@SpringBootApplication
@EnableRetry
public class GithubScoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubScoreApplication.class, args);
	}

}
