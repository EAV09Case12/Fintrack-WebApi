package com.example.fintrack_webapi.adapters.controller;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.fintrack_webapi.domain.port.input.ObtenerReporteUseCasePort;
import com.example.fintrack_webapi.domain.port.input.ReporteUseCasePort;

@WebMvcTest(ReporteController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReporteUseCasePort reporteUseCase;

    @MockitoBean
    private ObtenerReporteUseCasePort obtenerReporteUseCase;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("ana@test.com", null, List.of())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private ResultActions postReporte(int mes) throws Exception {
        return mockMvc.perform(post("/api/reportes/mensual")
                .param("mes", String.valueOf(mes)));
    }

    @Test
    void generaReporte() throws Exception {
        ArgumentCaptor<String> requestIdCaptor = ArgumentCaptor.forClass(String.class);

        postReporte(5)
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.mensaje").value("El reporte se está generando"))
                .andExpect(jsonPath("$.requestId").isNotEmpty());

        verify(reporteUseCase).generarReporteMensual(eq(5), requestIdCaptor.capture(), eq("ana@test.com"));

        String requestId = requestIdCaptor.getValue();
        assertNotNull(requestId);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 13})
    void mesInvalido(int mes) throws Exception {
        postReporte(mes)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El mes debe estar entre 1 y 12"));
    }

    @Test
    void descargaPdf() throws Exception {
        byte[] pdf = new byte[] { 1, 2, 3, 4 };
        when(obtenerReporteUseCase.obtenerReporte("abc123")).thenReturn(pdf);

        mockMvc.perform(get("/api/reportes/abc123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(Objects.requireNonNull(MediaType.APPLICATION_PDF)))
                .andExpect(header().string("Content-Disposition", "attachment; filename=reporte-financiero.pdf"))
                .andExpect(content().bytes(pdf));

        verify(obtenerReporteUseCase).obtenerReporte("abc123");
    }
}
