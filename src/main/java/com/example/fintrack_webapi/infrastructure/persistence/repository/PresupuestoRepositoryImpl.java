package com.example.fintrack_webapi.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;

import com.example.fintrack_webapi.domain.model.PresupuestoMensual;
import com.example.fintrack_webapi.domain.port.output.PresupuestoRepositoryPort;
import com.example.fintrack_webapi.infrastructure.persistence.entity.PresupuestoEntity;
import com.example.fintrack_webapi.infrastructure.persistence.mapper.PresupuestoMapper;
import com.example.fintrack_webapi.infrastructure.dao.PresupuestoJpaRepository;


@Repository
public class PresupuestoRepositoryImpl implements PresupuestoRepositoryPort{
 private final PresupuestoJpaRepository jpaRepository;

    public PresupuestoRepositoryImpl(PresupuestoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PresupuestoMensual guardar(PresupuestoMensual presupuesto) {
        System.out.println("=== DEBUG ===");
        System.out.println("Fecha: " + presupuesto.getFecha());
        System.out.println("Monto Total: " + presupuesto.getMontoTotal());
        System.out.println("Distribucion: " + presupuesto.obtenerDistribucion());
        
        PresupuestoEntity entity = PresupuestoMapper.toEntity(presupuesto);
        
        System.out.println("Entity montoTotal: " + entity.getMontoTotal());
        System.out.println("Entity serviciosCat: " + entity.getServiciosCat());
        
        PresupuestoEntity saved = jpaRepository.save(entity);
        /*PresupuestoEntity saved = jpaRepository.insertPresupuesto(entity);*/
        return PresupuestoMapper.toDomain(saved);
}
    /*public PresupuestoMensual guardar(PresupuestoMensual presupuesto) {

        PresupuestoEntity entity = PresupuestoMapper.toEntity(presupuesto);

        PresupuestoEntity saved = jpaRepository.insertPresupuesto(entity);

        return PresupuestoMapper.toDomain(saved);
    }*/
}
