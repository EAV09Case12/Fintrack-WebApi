package com.example.fintrack_webapi.adapters.controller;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.fintrack_webapi.domain.port.input.ObtenerReporteUseCasePort;
import com.example.fintrack_webapi.domain.port.input.ReporteUseCasePort;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ReporteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteUseCasePort reporteUseCase;

    @MockBean
    private ObtenerReporteUseCasePort obtenerReporteUseCase;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(
                "ana@test.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
            )
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void generaReporte() throws Exception {
        ArgumentCaptor<String> requestIdCaptor = ArgumentCaptor.forClass(String.class);

        mockMvc.perform(post("/api/reportes/mensual")
                        .param("mes", "6")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.mensaje").value("El reporte se está generando"))
                .andExpect(jsonPath("$.requestId").isNotEmpty());

        verify(reporteUseCase).generarReporteMensual(eq(6), requestIdCaptor.capture(), eq("ana@test.com"));
        assertNotNull(requestIdCaptor.getValue());
    }
}