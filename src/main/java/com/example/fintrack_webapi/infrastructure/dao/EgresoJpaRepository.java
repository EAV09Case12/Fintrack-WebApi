package com.example.fintrack_webapi.infrastructure.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.fintrack_webapi.infrastructure.persistence.entity.EgresoEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EgresoJpaRepository extends JpaRepository<EgresoEntity, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO egreso (monto, fecha, categoria, descripcion) " +
           "VALUES (:#{#entity.monto}, :#{#entity.fecha}, " +
           ":#{#entity.categoria}, :#{#entity.descripcion})",
           nativeQuery = true)
    void insertEgreso(@Param("entity") EgresoEntity entity);
}