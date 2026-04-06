package com.example.fintrack_webapi.adapters.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.fintrack_webapi.application.dto.commands.IngresoDTO;
import com.example.fintrack_webapi.application.dto.queries.PresupuestoResponseDTO;
import com.example.fintrack_webapi.application.usecase.TransaccionUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;

@WebMvcTest(TransaccionController.class)
class TransaccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransaccionUseCase transaccionUseCase;

    // Camino feliz: el controller recibe el JSON, delega al caso de uso y
    // devuelve el presupuesto en formato JSON.
    @Test
    void ingresoOk() throws Exception {
        Map<Integer, Double> porcentajes = new LinkedHashMap<>();
        porcentajes.put(1, 20.0);
        porcentajes.put(2, 15.0);
        porcentajes.put(3, 10.0);
        porcentajes.put(4, 20.0);
        porcentajes.put(5, 30.0);
        porcentajes.put(6, 5.0);

        IngresoDTO dto = new IngresoDTO(1000.0, "2026-03-29", porcentajes);

        // Respuesta simulada del caso de uso, como si ya hubiera calculado el presupuesto.
        Map<String, Double> respMontos = new LinkedHashMap<>();
        respMontos.put("SERVICIOS", 200.0);
        respMontos.put("ENTRETENIMIENTO", 150.0);
        respMontos.put("TRANSPORTE", 100.0);
        respMontos.put("ALIMENTACION", 200.0);
        respMontos.put("SALUD", 300.0);
        respMontos.put("DEUDAS", 50.0);

        PresupuestoResponseDTO resp = new PresupuestoResponseDTO(
                10L,
                "2026-03-29",
                1000.0,
            respMontos
        );

        when(transaccionUseCase.registrarIngreso(any(IngresoDTO.class))).thenReturn(resp);

        // Se envía el JSON real al endpoint para validar el binding y la respuesta.
        mockMvc.perform(post("/api/transacciones/SaveIngreso")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.fecha").value("2026-03-29"))
                .andExpect(jsonPath("$.montoTotal").value(1000.0))
                .andExpect(jsonPath("$.montosPorCategoria.SERVICIOS").value(200.0))
                .andExpect(jsonPath("$.montosPorCategoria.ENTRETENIMIENTO").value(150.0))
                .andExpect(jsonPath("$.montosPorCategoria.TRANSPORTE").value(100.0))
                .andExpect(jsonPath("$.montosPorCategoria.ALIMENTACION").value(200.0))
                .andExpect(jsonPath("$.montosPorCategoria.SALUD").value(300.0))
                .andExpect(jsonPath("$.montosPorCategoria.DEUDAS").value(50.0));

        // Verificamos que el controller haya delegado al caso de uso con el DTO correcto.
        ArgumentCaptor<IngresoDTO> arg = ArgumentCaptor.forClass(IngresoDTO.class);
        verify(transaccionUseCase).registrarIngreso(arg.capture());

        IngresoDTO capturado = arg.getValue();
        assertNotNull(capturado);
        assertEquals(1000.0, capturado.monto());
        assertEquals("2026-03-29", capturado.fecha());
        assertEquals(6, capturado.porcentajes().size());
        assertEquals(20.0, capturado.porcentajes().get(1));
        assertEquals(15.0, capturado.porcentajes().get(2));
        assertEquals(10.0, capturado.porcentajes().get(3));
        assertEquals(20.0, capturado.porcentajes().get(4));
        assertEquals(30.0, capturado.porcentajes().get(5));
        assertEquals(5.0, capturado.porcentajes().get(6));
    }

    // Caso de error: JSON mal formado.
    // Comportamiento esperado: devolver 400 y no invocar el caso de uso.
    // Comportamiento actual del codigo: Jackson falla al parsear el body y Spring responde 400.
    @Test
    void jsonInvalido400() throws Exception {
        String jsonInvalido = "{ \"monto\": 1000, \"fecha\": \"2026-03-29\", \"porcentajes\": {\"1\":20 }";

        mockMvc.perform(post("/api/transacciones/SaveIngreso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transaccionUseCase);
    }

    // Caso de error: body vacío.
    // Comportamiento esperado: devolver 400 y no invocar el caso de uso.
    // Comportamiento actual del codigo: @RequestBody no se puede deserializar sin contenido y Spring responde 400.
    @Test
    void bodyVacio400() throws Exception {
        mockMvc.perform(post("/api/transacciones/SaveIngreso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transaccionUseCase);
    }

    // Caso de error: el caso de uso lanza excepción.
    // Comportamiento esperado: devolver un error HTTP controlado (por ejemplo 4xx/5xx con mensaje uniforme).
    // Comportamiento actual del codigo: como no hay handler global, MockMvc propaga ServletException con causa RuntimeException.
    @Test
    void errorCasoUsoPropaga() {
        Map<Integer, Double> porcentajes = new LinkedHashMap<>();
        porcentajes.put(1, 100.0);

        IngresoDTO dto = new IngresoDTO(1000.0, "2026-03-29", porcentajes);

        when(transaccionUseCase.registrarIngreso(any(IngresoDTO.class)))
                .thenThrow(new RuntimeException("Error de negocio simulado"));

        ServletException ex = assertThrows(ServletException.class,
            () -> mockMvc.perform(post("/api/transacciones/SaveIngreso")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))));

        assertNotNull(ex.getCause());
        assertEquals(RuntimeException.class, ex.getCause().getClass());
        assertEquals("Error de negocio simulado", ex.getCause().getMessage());
    }
}