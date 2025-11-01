package com.rpassignment.githubscore.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

@Slf4j
@Configuration
public class RestTemplateConfig {

    @Value("${resttemplate.connect-timeout:3000}")
    private int connectTimeout;

    @Value("${resttemplate.read-timeout:5000}")
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        log.info("Initializing RestTemplate with connectTimeout={}ms and readTimeout={}ms", connectTimeout, readTimeout);

        ClientHttpRequestInterceptor loggingInterceptor = (
                request, body, execution
        ) -> {
            log.debug(" [{}] Request to {}", request.getMethod(), request.getURI());
            var response = execution.execute(request, body);
            log.debug("[{}] Response status: {}", request.getMethod(), response.getStatusCode());
            return response;
        };

        return restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .additionalInterceptors(List.of(loggingInterceptor))
                .build();
    }
}
