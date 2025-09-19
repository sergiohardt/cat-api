package com.sencon.catapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("${cat.api.base-url}")
    private String catApiBaseUrl;

    @Value("${cat.api.timeout:30s}")
    private Duration timeout;

    @Bean
    public WebClient catApiWebClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(timeout)
                .compress(true);

        return WebClient.builder()
                .baseUrl(catApiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }
}
