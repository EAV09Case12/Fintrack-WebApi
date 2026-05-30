package com.example.fintrack_webapi.adapters.handler;

import java.lang.reflect.Method;
import java.util.Objects;

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

    private void sample(String cantidad) {
        // Helper para construir un MethodParameter no nulo.
    }

    private MethodParameter typeMismatchParameter() throws NoSuchMethodException {
        Method method = Objects.requireNonNull(GlobalExceptionHandlerTest.class.getDeclaredMethod("sample", String.class));
        return new MethodParameter(Objects.requireNonNull(method), 0);
    }

    // Caso feliz: BadRequestException -> respuesta 400 con mensaje de negocio.
    // Comportamiento esperado: status 400 y path correcto.
    @Test
    void badReq400() {
        ResponseEntity<ErrorResponse> res = handler.handleBadRequest(
                new BadRequestException("dato inválido"),
                req("/api/x"));

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        ErrorResponse body = java.util.Objects.requireNonNull(res.getBody());
        assertNotNull(body);
        assertEquals("dato inválido", body.getMessage());
        assertEquals("/api/x", body.getPath());
    }

    // Caso feliz: ResourceNotFoundException -> respuesta 404.
    // Comportamiento esperado: status 404 y mensaje propagado.
    @Test
    void notFound404() {
        ResponseEntity<ErrorResponse> res = handler.handleNotFound(
                new ResourceNotFoundException("no existe"),
                req("/api/y"));

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        ErrorResponse body = java.util.Objects.requireNonNull(res.getBody());
        assertNotNull(body);
        assertEquals("no existe", body.getMessage());
    }

    // Caso de error de tipo: parámetro inválido.
    // Comportamiento esperado: status 400 con nombre del parámetro.
    @Test
    void typeMismatch400() throws Exception {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", Integer.class, "cantidad", Objects.requireNonNull(typeMismatchParameter()), new IllegalArgumentException());

        ResponseEntity<ErrorResponse> res = handler.handleTypeMismatch(ex, req("/api/z"));

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        ErrorResponse body = java.util.Objects.requireNonNull(res.getBody());
        assertNotNull(body);
        assertEquals("Parámetro inválido: cantidad", body.getMessage());
    }

    // Caso de error inesperado.
    // Comportamiento esperado: status 500 con mensaje genérico.
    @Test
    void general500() {
        ResponseEntity<ErrorResponse> res = handler.handleGeneral(
                new RuntimeException("boom"),
                req("/api/e"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
        ErrorResponse body = java.util.Objects.requireNonNull(res.getBody());
        assertNotNull(body);
        assertEquals("Error interno del servidor", body.getMessage());
    }
}
