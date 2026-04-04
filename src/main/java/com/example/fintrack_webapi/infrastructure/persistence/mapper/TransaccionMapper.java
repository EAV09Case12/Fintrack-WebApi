package com.example.fintrack_webapi.infrastructure.persistence.mapper;

import com.example.fintrack_webapi.domain.model.Egreso;
import com.example.fintrack_webapi.domain.model.Ingreso;
import com.example.fintrack_webapi.domain.model.Transaccion;
import com.example.fintrack_webapi.domain.model.Categoria;

import com.example.fintrack_webapi.infrastructure.persistence.entity.IngresoEntity;
import com.example.fintrack_webapi.infrastructure.persistence.entity.EgresoEntity;
import com.example.fintrack_webapi.infrastructure.persistence.entity.MovimientoEntity;

public class TransaccionMapper {

    // =========================
    // DOMAIN → ENTITY
    // =========================

    public static IngresoEntity toEntityIngreso(Ingreso ingreso) {

        IngresoEntity entity = new IngresoEntity();
        entity.setMonto(ingreso.getMonto());
        entity.setFecha(ingreso.getFecha());

        return entity;
    }

    public static EgresoEntity toEntityEgreso(Egreso egreso) {

        EgresoEntity entity = new EgresoEntity();
        entity.setMonto(egreso.getMonto());
        entity.setFecha(egreso.getFecha());

        // 🔥 ahora es int, no entity
        entity.setCategoria(egreso.getCategoria().getCodigo());

        entity.setDescripcion(egreso.getDescripcion());

        return entity;
    }

    // =========================
    // ENTITY → DOMAIN
    // =========================

    public static Ingreso toDomain(IngresoEntity entity) {
        return new Ingreso(
                entity.getMonto(),
                entity.getFecha()
        );
    }

    public static Egreso toDomain(EgresoEntity entity) {

        return new Egreso(
                entity.getMonto(),
                entity.getFecha(),
                buscarPorCodigo(entity.getCategoria()), // 🔥 cambio clave
                entity.getDescripcion()
        );
    }

    // =========================
    // MOVIMIENTO → DOMAIN
    // =========================

    public static Transaccion toDomain(MovimientoEntity entity) {
        Integer cat = entity.getCategoria();

        if (cat != null && cat.intValue() != 0) {
            // es egreso
            return new Egreso(
                    entity.getMonto(),
                    entity.getFecha(),
                    buscarPorCodigo(cat.intValue()),
                    entity.getDescripcion()
            );
        } else {
            // es ingreso
            return new Ingreso(
                    entity.getMonto(),
                    entity.getFecha()
            );
        }
    }

    // =========================
    // MÉTODO AUXILIAR 🔥
    // =========================

    private static Categoria buscarPorCodigo(int codigo) {

        for (Categoria c : Categoria.values()) {
            if (c.getCodigo() == codigo) {
                return c;
            }
        }

        throw new RuntimeException("Categoría inválida: " + codigo);
    }
}