package com.example.fintrack_webapi.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;

import com.example.fintrack_webapi.domain.model.PresupuestoMensual;
import com.example.fintrack_webapi.domain.port.output.PresupuestoRepositoryPort;
import com.example.fintrack_webapi.infrastructure.persistence.entity.PresupuestoEntity;
import com.example.fintrack_webapi.infrastructure.persistence.mapper.PresupuestoMapper;
import com.example.fintrack_webapi.infrastructure.dao.PresupuestoJpaRepository;

import java.util.Date;
import java.util.List;

@Repository
public class PresupuestoRepositoryImpl implements PresupuestoRepositoryPort{
    private final PresupuestoJpaRepository jpaRepository;

    public PresupuestoRepositoryImpl(PresupuestoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PresupuestoMensual guardar(PresupuestoMensual presupuesto) {
        List<PresupuestoEntity> entities = PresupuestoMapper.toEntities(presupuesto);
        List<PresupuestoEntity> saved = jpaRepository.saveAll(entities);
        return PresupuestoMapper.toDomain(saved);
    }

    @Override
    public PresupuestoMensual obtenerPorFecha(Date fecha) {
        List<PresupuestoEntity> rows = jpaRepository.findByFecha(fecha);
        return PresupuestoMapper.toDomain(rows);
    }
}
