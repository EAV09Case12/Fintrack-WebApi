package com.example.fintrack_webapi.infrastructure.security;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.impl.DefaultClaims;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @AfterEach
    void clean() {
        SecurityContextHolder.clearContext();
    }

    // Caso feliz: token válido en header.
    // Comportamiento esperado: autentica usuario con roles.
    @Test
    void tokenValidoAutentica() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer tok");

        DefaultClaims claims = new DefaultClaims();
        claims.setSubject("ana");
        claims.put("roles", List.of("ADMIN"));
        when(jwtService.extractAllClaims("tok")).thenReturn(claims);

        filter.doFilter(req, new MockHttpServletResponse(), new MockFilterChain());

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertEquals("ana", auth.getPrincipal());
        assertEquals("ROLE_ADMIN", auth.getAuthorities().iterator().next().getAuthority());
    }

    // Caso de error: token inválido.
    // Comportamiento esperado: limpia contexto y no rompe flujo.
    @Test
    void tokenInvalidoLimpia() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer tok");
        when(jwtService.extractAllClaims("tok")).thenThrow(new RuntimeException("bad"));

        filter.doFilter(req, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // Caso borde: sin header Authorization.
    // Comportamiento esperado: no autentica y continúa el chain.
    @Test
    void sinHeaderNoAutentica() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);

        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
