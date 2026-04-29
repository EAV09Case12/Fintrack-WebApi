package com.example.fintrack_webapi.infrastructure.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fintrack_webapi.infrastructure.persistence.entity.IngresoEntity;

public interface IngresoJpaRepository extends JpaRepository<IngresoEntity, Long> {
        @Query("""
        SELECT i FROM IngresoEntity i
        WHERE EXTRACT(MONTH FROM i.fecha) = :mes
        AND EXTRACT(YEAR FROM i.fecha) = :anio
    """)
    List<IngresoEntity> findByMesAndAnio(
        @Param("mes") int mes,
        @Param("anio") int anio
    );
}
