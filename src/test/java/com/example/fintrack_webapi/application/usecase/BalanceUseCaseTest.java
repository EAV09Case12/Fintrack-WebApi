package com.example.fintrack_webapi.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.fintrack_webapi.domain.exception.BadRequestException;
import com.example.fintrack_webapi.domain.port.output.BalanceRepositoryPort;

@ExtendWith(MockitoExtension.class)
class BalanceUseCaseTest {

    @Mock
    private BalanceRepositoryPort repo;

    @InjectMocks
    private BalanceUseCase useCase;

    // Caso feliz: calcula balance y porcentajes.
    // Comportamiento esperado: ingresos, gastos y ahorro consistentes.
    @Test
    void balanceOk() {
        when(repo.obtenerTotalIngresos(3, 2026)).thenReturn(1000.0);
        when(repo.obtenerTotalGastos(3, 2026)).thenReturn(250.0);

        var dto = useCase.obtenerBalance("2026-03-10");

        assertEquals(1000.0, dto.totalIngresos());
        assertEquals(250.0, dto.totalGastos());
        assertEquals(750.0, dto.balance());
        assertEquals(25.0, dto.porcentajeGastos());
        assertEquals(75.0, dto.porcentajeAhorro());
    }

    // Caso borde: ingresos en cero.
    // Comportamiento esperado: porcentajes en 0 para evitar división.
    @Test
    void ingresosCero() {
        when(repo.obtenerTotalIngresos(4, 2026)).thenReturn(0.0);
        when(repo.obtenerTotalGastos(4, 2026)).thenReturn(300.0);

        var dto = useCase.obtenerBalance("2026-04-15");

        assertEquals(0.0, dto.porcentajeGastos());
        assertEquals(0.0, dto.porcentajeAhorro());
    }

    // Caso de error: fecha inválida.
    // Comportamiento esperado: BadRequestException con mensaje claro.
    @Test
    void fechaInvalida() {
        assertThrows(BadRequestException.class, () -> useCase.obtenerBalance("2026/01/01"));
    }
}
