package com.example.fintrack_webapi.infrastructure.persistence.mapper;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.example.fintrack_webapi.domain.model.Categoria;
import com.example.fintrack_webapi.domain.model.Egreso;
import com.example.fintrack_webapi.domain.model.Ingreso;
import com.example.fintrack_webapi.infrastructure.persistence.entity.EgresoEntity;
import com.example.fintrack_webapi.infrastructure.persistence.entity.IngresoEntity;
import com.example.fintrack_webapi.infrastructure.persistence.entity.MovimientoEntity;

class TransaccionMapperTest {

    private final Date f = new Date();

    // Caso feliz: mapeo ingreso domain -> entity.
    // Comportamiento esperado: conserva monto y fecha.
    @Test
    void ingresoToEntity() {
        Ingreso in = new Ingreso(120.0, f);
        IngresoEntity e = TransaccionMapper.toEntityIngreso(in);

        assertEquals(120.0, e.getMonto());
        assertEquals(f, e.getFecha());
    }

    // Caso feliz: mapeo egreso domain -> entity.
    // Comportamiento esperado: conserva categoría y descripción.
    @Test
    void egresoToEntity() {
        Egreso eg = new Egreso(90.0, f, Categoria.SALUD, "medicinas");
        EgresoEntity e = TransaccionMapper.toEntityEgreso(eg);

        assertEquals(90.0, e.getMonto());
        assertEquals(Categoria.SALUD.getCodigo(), e.getIdcat());
        assertEquals("medicinas", e.getDescripcion());
    }

    // Caso feliz: entity -> domain para ingreso y egreso.
    // Comportamiento esperado: reconstruye objetos de dominio.
    @Test
    void entityToDomain() {
        IngresoEntity in = new IngresoEntity(1L, 500.0, f);
        EgresoEntity eg = new EgresoEntity(2L, 200.0, f, 4, "mercado");

        assertEquals(500.0, TransaccionMapper.toDomain(in).getMonto());
        assertEquals(Categoria.ALIMENTACION, TransaccionMapper.toDomain(eg).getCategoria());
    }

    // Caso de error: categoría inválida en egreso entity.
    // Comportamiento esperado: lanza RuntimeException.
    @Test
    void categoriaInvalida() {
        EgresoEntity eg = new EgresoEntity(2L, 200.0, f, 999, "x");
        assertThrows(RuntimeException.class, () -> TransaccionMapper.toDomain(eg));
    }

    // Caso no soportado: movimiento entity genérico.
    // Comportamiento esperado: UnsupportedOperationException.
    @Test
    void movimientoNoSoportado() {
        MovimientoEntity m = new MovimientoEntity("ingreso", 1);
        assertThrows(UnsupportedOperationException.class, () -> TransaccionMapper.toDomain(m));
    }
}
