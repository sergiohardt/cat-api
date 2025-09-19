package com.sencon.catapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8090}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Desenvolvimento"),
                        new Server().url("http://cat-api:8090").description("Docker"),
                        new Server().url("http://ec2-100-24-9-6.compute-1.amazonaws.com:8090").description("ec2")
                ))
                .info(new Info()
                        .title("Cat API - CQRS")
                        .description("API para coleta e consulta de dados de raças de gatos usando arquitetura CQRS")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Cat API Team")
                                .email("team@catapi.com"))
                );
    }
}
