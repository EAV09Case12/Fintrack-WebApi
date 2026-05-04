package com.example.fintrack_webapi.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
            .components(new Components()
                // 🔐 Security
                .addSecuritySchemes(securitySchemeName,
                    new SecurityScheme()
                        .name("Authorization")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )

                // ❗ RESPUESTAS GLOBALES LIMPIAS
                .addResponses("Unauthorized",
                    new ApiResponse()
                        .description("No autenticado")
                        .content(new Content()) // ← SIN body
                )
                .addResponses("Forbidden",
                    new ApiResponse()
                        .description("Sin permisos")
                        .content(new Content()) // ← SIN body
                )
            );
    }
}