package com.example.fintrack_webapi.infrastructure.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.fintrack_webapi.infrastructure.persistence.entity.PresupuestoEntity;
import java.util.Date;
import java.util.List;

public interface PresupuestoJpaRepository extends JpaRepository<PresupuestoEntity, PresupuestoEntity.PresupuestoId> {
    List<PresupuestoEntity> findByFecha(Date fecha);
}