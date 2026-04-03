package com.example.fintrack_webapi.infrastructure.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.fintrack_webapi.infrastructure.persistence.entity.PresupuestoEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PresupuestoJpaRepository  extends JpaRepository<PresupuestoEntity, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO presupuesto " +
           "(fecha, montototal, servicioscat, entretenimientocat, transportecat, " +
           "alimentacioncat, saludcat, deudascat) " +
           "VALUES (:#{#entity.fecha}, :#{#entity.montoTotal}, " +
           ":#{#entity.serviciosCat}, :#{#entity.entretenimientoCat}, " +
           ":#{#entity.transporteCat}, :#{#entity.alimentacionCat}, " +
           ":#{#entity.saludCat}, :#{#entity.deudasCat})",
           nativeQuery = true)
    void insertPresupuesto(@Param("entity") PresupuestoEntity entity);
}