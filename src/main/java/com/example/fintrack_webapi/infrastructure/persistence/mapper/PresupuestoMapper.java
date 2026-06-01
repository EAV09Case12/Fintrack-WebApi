package com.example.fintrack_webapi.infrastructure.persistence.mapper;

import com.example.fintrack_webapi.domain.model.Categoria;
import com.example.fintrack_webapi.domain.model.PresupuestoMensual;
import com.example.fintrack_webapi.infrastructure.persistence.entity.PresupuestoEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class PresupuestoMapper {

    public static List<PresupuestoEntity> toEntities(
            PresupuestoMensual domain,
            String userEmail
    ) {

        List<PresupuestoEntity> entities = new ArrayList<>();

        Map<Categoria, Double> dist = domain.obtenerDistribucion();

        for (Map.Entry<Categoria, Double> entry : dist.entrySet()) {

            PresupuestoEntity e = new PresupuestoEntity();

            e.setFecha(domain.getFecha());

            e.setMonto(
                    BigDecimal.valueOf(entry.getValue())
                            .setScale(2, RoundingMode.HALF_UP)
            );

            e.setIdCat(entry.getKey().getCodigo());

            e.setUserEmail(userEmail);

            entities.add(e);
        }

        return entities;
    }

    public static PresupuestoMensual toDomain(List<PresupuestoEntity> entities) {

        if (entities == null || entities.isEmpty()) {
            // Retornar un objeto vacío consistente en lugar de null
            return new PresupuestoMensual(new Date(), 0.0, new HashMap<>());
        }

        Map<Categoria, Double> montos = new HashMap<>();

        double montoTotal = 0.0;

        Date fecha = entities.get(0).getFecha();

        for (PresupuestoEntity e : entities) {

            BigDecimal m = e.getMonto();

            double val = m == null ? 0.0 : m.doubleValue();

            montoTotal += val;

            Categoria cat = buscarPorCodigo(e.getIdCat());

            if (cat != null) {
                montos.put(cat, val);
            }
        }

        return new PresupuestoMensual(fecha, montoTotal, montos);
    }

    private static Categoria buscarPorCodigo(int codigo) {

        for (Categoria c : Categoria.values()) {

            if (c.getCodigo() == codigo) {
                return c;
            }
        }

        return null;
    }
}