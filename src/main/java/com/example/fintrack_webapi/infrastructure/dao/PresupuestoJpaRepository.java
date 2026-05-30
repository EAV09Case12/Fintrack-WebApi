package com.example.fintrack_webapi.infrastructure.dao;

import com.example.fintrack_webapi.infrastructure.persistence.entity.PresupuestoEntity;
import com.example.fintrack_webapi.infrastructure.persistence.entity.PresupuestoEntity.PresupuestoId;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PresupuestoJpaRepository
        extends JpaRepository<PresupuestoEntity, PresupuestoId> {

    List<PresupuestoEntity> findByFecha(Date fecha);

    List<PresupuestoEntity> findByFechaBetween(Date inicio, Date fin);
}