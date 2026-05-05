package com.example.fintrack_webapi.adapters.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.fintrack_webapi.domain.exception.BadRequestException;
import com.example.fintrack_webapi.domain.exception.ResourceNotFoundException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private MockHttpServletRequest req(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(path);
        return request;
    }

    // Caso feliz: BadRequestException -> respuesta 400 con mensaje de negocio.
    // Comportamiento esperado: status 400 y path correcto.
    @Test
    void badReq400() {
        ResponseEntity<ErrorResponse> res = handler.handleBadRequest(
                new BadRequestException("dato inválido"),
                req("/api/x"));

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals("dato inválido", res.getBody().getMessage());
        assertEquals("/api/x", res.getBody().getPath());
    }

    // Caso feliz: ResourceNotFoundException -> respuesta 404.
    // Comportamiento esperado: status 404 y mensaje propagado.
    @Test
    void notFound404() {
        ResponseEntity<ErrorResponse> res = handler.handleNotFound(
                new ResourceNotFoundException("no existe"),
                req("/api/y"));

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals("no existe", res.getBody().getMessage());
    }

    // Caso de error de tipo: parámetro inválido.
    // Comportamiento esperado: status 400 con nombre del parámetro.
    @Test
    void typeMismatch400() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", Integer.class, "cantidad", (MethodParameter) null, new IllegalArgumentException());

        ResponseEntity<ErrorResponse> res = handler.handleTypeMismatch(ex, req("/api/z"));

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals("Parámetro inválido: cantidad", res.getBody().getMessage());
    }

    // Caso de error inesperado.
    // Comportamiento esperado: status 500 con mensaje genérico.
    @Test
    void general500() {
        ResponseEntity<ErrorResponse> res = handler.handleGeneral(
                new RuntimeException("boom"),
                req("/api/e"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals("Error interno del servidor", res.getBody().getMessage());
    }
}
