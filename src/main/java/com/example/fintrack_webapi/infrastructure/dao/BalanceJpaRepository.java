package com.example.fintrack_webapi.infrastructure.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fintrack_webapi.infrastructure.persistence.entity.MovimientoEntity;

import java.util.List;

public interface BalanceJpaRepository extends JpaRepository<MovimientoEntity, MovimientoEntity.MovimientoId> {

    @Query(value = """
        SELECT 
            COALESCE(SUM(CASE WHEN LOWER(m.tipotransferencia) = 'ingreso' THEN i.monto ELSE 0 END), 0) as ingresos,
            COALESCE(SUM(CASE WHEN LOWER(m.tipotransferencia) = 'egreso' THEN e.monto ELSE 0 END), 0) as gastos
        FROM movimiento m
        LEFT JOIN ingreso i 
            ON LOWER(m.tipotransferencia) = 'ingreso' 
            AND m.idtransferencia = i.id
        LEFT JOIN egreso e 
            ON LOWER(m.tipotransferencia) = 'egreso' 
            AND m.idtransferencia = e.id
        WHERE EXTRACT(MONTH FROM COALESCE(i.fecha, e.fecha)) = :mes
          AND EXTRACT(YEAR FROM COALESCE(i.fecha, e.fecha)) = :anio
    """, nativeQuery = true)
    List<Object[]> obtenerBalanceMensual(
        @Param("mes") int mes,
        @Param("anio") int anio
    );
}