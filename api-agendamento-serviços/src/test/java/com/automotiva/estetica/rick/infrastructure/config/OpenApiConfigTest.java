package com.automotiva.estetica.rick.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes de OpenApiConfig")
class OpenApiConfigTest {

    private final OpenApiConfig openApiConfig = new OpenApiConfig();

    @Test
    @DisplayName("deve configurar schema bearer e metadata principal")
    void customOpenAPI_deveConfigurarBearerEMetadata() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        assertNotNull(openAPI);
        assertTrue(openAPI.getInfo().getTitle().contains("Rick"));
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertFalse(openAPI.getSecurity().isEmpty());
        assertTrue(openAPI.getSecurity().getFirst().containsKey("bearerAuth"));

        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");
        assertNotNull(securityScheme);
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("bearer", securityScheme.getScheme());
        assertEquals("JWT", securityScheme.getBearerFormat());
    }
}


