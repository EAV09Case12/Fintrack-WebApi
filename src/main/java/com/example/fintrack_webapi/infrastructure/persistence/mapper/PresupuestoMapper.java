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
        // Crear mapa con los montos guardados
        Map<Categoria, Double> montos = new HashMap<>();
        
        if (entity.getServiciosCat() != null)
            montos.put(Categoria.SERVICIOS, entity.getServiciosCat());
        if (entity.getEntretenimientoCat() != null)
            montos.put(Categoria.ENTRETENIMIENTO, entity.getEntretenimientoCat());
        if (entity.getTransporteCat() != null)
            montos.put(Categoria.TRANSPORTE, entity.getTransporteCat());
        if (entity.getAlimentacionCat() != null)
            montos.put(Categoria.ALIMENTACION, entity.getAlimentacionCat());
        if (entity.getSaludCat() != null)
            montos.put(Categoria.SALUD, entity.getSaludCat());
        if (entity.getDeudasCat() != null)
            montos.put(Categoria.DEUDAS, entity.getDeudasCat());
        
        // Usar un constructor especial para reconstrucción
        return new PresupuestoMensual(entity.getFecha(), entity.getMontoTotal(), montos);
    }
}