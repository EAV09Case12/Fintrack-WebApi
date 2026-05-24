package com.example.fintrack_webapi.infrastructure.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fintrack_webapi.infrastructure.persistence.entity.MovimientoEntity;

import java.util.List;

public interface MovimientoJpaRepository
        extends JpaRepository<MovimientoEntity, Long> {

    List<MovimientoEntity> findByUserEmail(String userEmail);

    @Query(value = """
        SELECT
            COALESCE(i.id, e.id) as id,

            CASE
                WHEN lower(m.tipotransferencia) = 'ingreso'
                THEN 'INGRESO'
                ELSE 'EGRESO'
            END as tipo,

            COALESCE(i.monto, e.monto) as monto,

            COALESCE(i.fecha, e.fecha) as fecha,

            c.nombre as categoria,

            e.descripcion as descripcion

        FROM aud_movimiento m

        LEFT JOIN ingreso i
            ON lower(m.tipotransferencia) = 'ingreso'
            AND m.idtransferencia = i.id

        LEFT JOIN egreso e
            ON lower(m.tipotransferencia) = 'egreso'
            AND m.idtransferencia = e.id

        LEFT JOIN categoria c
            ON e.idcat = c.id

        WHERE m.user_email = :email

        ORDER BY fecha DESC
    """, nativeQuery = true)
    List<Object[]> fetchHistorialNative(
            @Param("email") String email
    );

    @Query(value = """
        SELECT
            COALESCE(i.id, e.id) as id,

            CASE
                WHEN lower(m.tipotransferencia) = 'ingreso'
                THEN 'INGRESO'
                ELSE 'EGRESO'
            END as tipo,

            COALESCE(i.monto, e.monto) as monto,

            COALESCE(i.fecha, e.fecha) as fecha,

            c.nombre as categoria,

            e.descripcion as descripcion

        FROM aud_movimiento m

        LEFT JOIN ingreso i
            ON lower(m.tipotransferencia) = 'ingreso'
            AND m.idtransferencia = i.id

        LEFT JOIN egreso e
            ON lower(m.tipotransferencia) = 'egreso'
            AND m.idtransferencia = e.id

        LEFT JOIN categoria c
            ON e.idcat = c.id

        WHERE e.idcat = :cat
        AND m.user_email = :email

        ORDER BY fecha DESC
    """, nativeQuery = true)
    List<Object[]> fetchByCategoriaNative(
            @Param("cat") int categoria,
            @Param("email") String email
    );
}