package com.example.fintrack_webapi.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private JwtService service;
    private String secret;

    @BeforeEach
    void setUp() {
        service = new JwtService();
        secret = Base64.getEncoder().encodeToString(
                "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(service, "secret", secret);
    }

    private String token(String sub, Object roles) {
        return Jwts.builder()
                .setSubject(sub)
                .claim("roles", roles)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)), SignatureAlgorithm.HS256)
                .compact();
    }

    // Caso feliz: token válido con roles.
    // Comportamiento esperado: extrae usuario y roles correctamente.
    @Test
    void extraeUserYRoles() {
        String t = token("ana", List.of("USER", "ADMIN"));

        assertEquals("ana", service.extractUser(t));
        assertEquals(List.of("USER", "ADMIN"), service.extractRoles(t));
        assertTrue(service.isTokenValid(t));
    }

    // Caso borde: claim roles no es lista.
    // Comportamiento esperado: retorna lista vacía.
    @Test
    void rolesNoLista() {
        String t = token("ana", "USER");
        assertTrue(service.extractRoles(t).isEmpty());
    }

    // Caso de error: token inválido.
    // Comportamiento esperado: validación false.
    @Test
    void tokenInvalido() {
        assertFalse(service.isTokenValid("token-malformado"));
    }
}
