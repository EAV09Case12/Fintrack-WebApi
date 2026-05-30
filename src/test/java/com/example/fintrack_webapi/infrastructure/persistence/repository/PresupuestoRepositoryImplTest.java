package com.example.fintrack_webapi.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.fintrack_webapi.domain.model.Categoria;
import com.example.fintrack_webapi.domain.model.PresupuestoMensual;
import com.example.fintrack_webapi.infrastructure.dao.PresupuestoJpaRepository;
import com.example.fintrack_webapi.infrastructure.persistence.entity.PresupuestoEntity;

@ExtendWith(MockitoExtension.class)
class PresupuestoRepositoryImplTest {

    @Mock
    private PresupuestoJpaRepository jpa;

    @InjectMocks
    private PresupuestoRepositoryImpl repo;

    private void autenticarComo(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, List.of())
        );
    }

    // Caso feliz: guardar presupuesto mensual.
    // Comportamiento esperado: guarda filas y retorna dominio.
    @Test
    void guardaOk() {
        Date f = new Date();
        PresupuestoMensual p = new PresupuestoMensual(f, 1000.0, Map.of(Categoria.SERVICIOS, 1000.0));

        autenticarComo("presupuesto@test.com");

        when(jpa.saveAll(anyList())).thenReturn(List.of(new PresupuestoEntity(f, 1, "presupuesto@test.com", new BigDecimal("1000.00"))));

        PresupuestoMensual out = repo.guardar(p);
        assertEquals(1000.0, out.getMontoTotal());
    }

    // Caso feliz: consultar por fecha.
    // Comportamiento esperado: mapea filas a dominio.
    @Test
    void porFechaOk() {
        Date f = new Date();
        autenticarComo("presupuesto@test.com");
        when(jpa.findByFechaBetween(org.mockito.ArgumentMatchers.any(Date.class), org.mockito.ArgumentMatchers.any(Date.class))).thenReturn(List.of(new PresupuestoEntity(f, 4, "presupuesto@test.com", new BigDecimal("500.00"))));

        PresupuestoMensual out = repo.obtenerPorFecha(f);
        assertEquals(500.0, out.getMontoTotal());
    }
}
