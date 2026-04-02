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

        PresupuestoEntity entity = PresupuestoMapper.toEntity(presupuesto);

        PresupuestoEntity saved = jpaRepository.save(entity);

        return PresupuestoMapper.toDomain(saved);
    }
}
