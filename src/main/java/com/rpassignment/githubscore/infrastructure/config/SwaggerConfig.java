package com.rpassignment.githubscore.infrastructure.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI githubScoreOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GitHub Score API")
                        .description("API to calculate repository popularity score from GitHub")
                        .version("1.0.0"));
    }
}
