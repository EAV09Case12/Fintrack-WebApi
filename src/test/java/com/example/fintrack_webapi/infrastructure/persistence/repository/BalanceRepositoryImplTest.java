package com.example.fintrack_webapi.infrastructure.persistence.repository;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.fintrack_webapi.infrastructure.dao.EgresoJpaRepository;
import com.example.fintrack_webapi.infrastructure.dao.IngresoJpaRepository;
import com.example.fintrack_webapi.infrastructure.persistence.entity.EgresoEntity;
import com.example.fintrack_webapi.infrastructure.persistence.entity.IngresoEntity;

@ExtendWith(MockitoExtension.class)
class BalanceRepositoryImplTest {

    @Mock
    private IngresoJpaRepository ingresoRepo;

    @Mock
    private EgresoJpaRepository egresoRepo;

    @InjectMocks
    private BalanceRepositoryImpl repo;

    // Caso feliz: suma de ingresos del periodo.
    // Comportamiento esperado: total correcto.
    @Test
    void totalIngresos() {
        when(ingresoRepo.findByMesAndAnio(3, 2026)).thenReturn(List.of(
                new IngresoEntity(1L, 100.0, new Date()),
                new IngresoEntity(2L, 250.0, new Date())));

        assertEquals(350.0, repo.obtenerTotalIngresos(3, 2026));
    }

    // Caso feliz: suma de gastos del periodo.
    // Comportamiento esperado: total correcto.
    @Test
    void totalGastos() {
        when(egresoRepo.findByMesAndAnio(3, 2026)).thenReturn(List.of(
                new EgresoEntity(1L, 80.0, new Date(), 1, "a"),
                new EgresoEntity(2L, 20.0, new Date(), 2, "b")));

        assertEquals(100.0, repo.obtenerTotalGastos(3, 2026));
    }
}
