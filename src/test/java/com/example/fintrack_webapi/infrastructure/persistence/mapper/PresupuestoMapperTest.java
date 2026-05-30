package com.example.fintrack_webapi.infrastructure.persistence.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import com.example.fintrack_webapi.domain.model.Categoria;
import com.example.fintrack_webapi.domain.model.PresupuestoMensual;
import com.example.fintrack_webapi.infrastructure.persistence.entity.PresupuestoEntity;

class PresupuestoMapperTest {

    // Caso feliz: domain -> entities.
    // Comportamiento esperado: genera filas por categoría con escala 2.
    @Test
    void toEntitiesOk() {
        Date f = new Date();
        PresupuestoMensual p = new PresupuestoMensual(
                f,
                1000.0,
                Map.of(Categoria.SERVICIOS, 333.335, Categoria.SALUD, 666.665));

        List<PresupuestoEntity> rows = PresupuestoMapper.toEntities(p, "ana@test.com");

        assertEquals(2, rows.size());
        assertEquals(2, rows.get(0).getMonto().scale());
    }

    // Caso feliz: entities -> domain.
    // Comportamiento esperado: suma total y mapa por categoría.
    @Test
    void toDomainOk() {
        Date f = new Date();
        PresupuestoEntity a = new PresupuestoEntity(f, 1, "ana@test.com", new BigDecimal("250.00"));
        PresupuestoEntity b = new PresupuestoEntity(f, 4, "ana@test.com", new BigDecimal("750.00"));

        PresupuestoMensual p = PresupuestoMapper.toDomain(List.of(a, b));

        assertEquals(1000.0, p.getMontoTotal());
        assertEquals(250.0, p.obtenerDistribucion().get(Categoria.SERVICIOS));
        assertEquals(750.0, p.obtenerDistribucion().get(Categoria.ALIMENTACION));
    }

    // Caso borde: lista vacía o nula.
    // Comportamiento esperado: retorna null.
    @Test
    void toDomainVacio() {
        assertNull(PresupuestoMapper.toDomain(List.of()));
        assertNull(PresupuestoMapper.toDomain(null));
    }
}
