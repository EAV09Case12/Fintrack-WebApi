package com.example.fintrack_webapi.infrastructure.persistence.mapper;

import com.example.fintrack_webapi.domain.model.Categoria;
import com.example.fintrack_webapi.domain.model.Ingreso;
import com.example.fintrack_webapi.domain.model.PresupuestoMensual;
import com.example.fintrack_webapi.infrastructure.persistence.entity.PresupuestoEntity;

import java.util.HashMap;
import java.util.Map;

public class PresupuestoMapper {

    // =========================
    // DOMAIN → ENTITY
    // =========================

    public static PresupuestoEntity toEntity(PresupuestoMensual domain) {

        PresupuestoEntity entity = new PresupuestoEntity();
        entity.setFecha(domain.getFecha());
        entity.setMontoTotal(domain.getMontoTotal());

        Map<Categoria, Double> dist = domain.obtenerDistribucion();

        entity.setServiciosCat(dist.get(Categoria.SERVICIOS));
        entity.setEntretenimientoCat(dist.get(Categoria.ENTRETENIMIENTO));
        entity.setTransporteCat(dist.get(Categoria.TRANSPORTE));
        entity.setAlimentacionCat(dist.get(Categoria.ALIMENTACION));
        entity.setSaludCat(dist.get(Categoria.SALUD));
        entity.setDeudasCat(dist.get(Categoria.DEUDAS));

        return entity;
    }

    // =========================
    // ENTITY → DOMAIN
    // =========================

    public static PresupuestoMensual toDomain(PresupuestoEntity entity) {

        Ingreso ingreso = new Ingreso(
                entity.getMontoTotal(),
                entity.getFecha()
        );

        PresupuestoMensual domain = new PresupuestoMensual(ingreso);

        Map<Categoria, Double> distribucion = new HashMap<>();

        if (entity.getServiciosCat() != null)
            distribucion.put(Categoria.SERVICIOS, entity.getServiciosCat());

        if (entity.getEntretenimientoCat() != null)
            distribucion.put(Categoria.ENTRETENIMIENTO, entity.getEntretenimientoCat());

        if (entity.getTransporteCat() != null)
            distribucion.put(Categoria.TRANSPORTE, entity.getTransporteCat());

        if (entity.getAlimentacionCat() != null)
            distribucion.put(Categoria.ALIMENTACION, entity.getAlimentacionCat());

        if (entity.getSaludCat() != null)
            distribucion.put(Categoria.SALUD, entity.getSaludCat());

        if (entity.getDeudasCat() != null)
            distribucion.put(Categoria.DEUDAS, entity.getDeudasCat());

        domain.distribuir(distribucion);

        return domain;
    }
}