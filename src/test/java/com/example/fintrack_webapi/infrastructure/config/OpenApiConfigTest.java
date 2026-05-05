package com.example.fintrack_webapi.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class OpenApiConfigTest {

    // Caso feliz: configuración OpenAPI base.
    // Comportamiento esperado: esquema bearer y respuestas globales.
    @Test
    void openApiBase() {
        OpenApiConfig cfg = new OpenApiConfig();
        var api = cfg.customOpenAPI();

        assertNotNull(api.getComponents());
        assertTrue(api.getComponents().getSecuritySchemes().containsKey("bearerAuth"));
        assertEquals("No autenticado", api.getComponents().getResponses().get("Unauthorized").getDescription());
        assertEquals("Sin permisos", api.getComponents().getResponses().get("Forbidden").getDescription());
    }
}
