package com.example.fintrack_webapi.infrastructure.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.fintrack_webapi.infrastructure.persistence.entity.MovimientoEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovimientoJpaRepository extends JpaRepository<MovimientoEntity, MovimientoEntity.MovimientoId> {
	List<MovimientoEntity> findAll();

	@Query(value = "SELECT coalesce(i.id, e.id) as id, " +
			"CASE WHEN lower(m.tipotransferencia) = 'ingreso' THEN 'INGRESO' ELSE 'EGRESO' END as tipo, " +
			"coalesce(i.monto, e.monto) as monto, coalesce(i.fecha, e.fecha) as fecha, " +
			"c.nombre as categoria, e.descripcion as descripcion " +
			"FROM movimiento m " +
			"LEFT JOIN ingreso i ON lower(m.tipotransferencia) = 'ingreso' AND m.idtransferencia = i.id " +
			"LEFT JOIN egreso e ON lower(m.tipotransferencia) = 'egreso' AND m.idtransferencia = e.id " +
			"LEFT JOIN categoria c ON e.idcat = c.id " +
			"ORDER BY fecha DESC",
			nativeQuery = true)
	List<Object[]> fetchHistorialNative();

	@Query(value = "SELECT coalesce(i.id, e.id) as id, " +
			"CASE WHEN lower(m.tipotransferencia) = 'ingreso' THEN 'INGRESO' ELSE 'EGRESO' END as tipo, " +
			"coalesce(i.monto, e.monto) as monto, coalesce(i.fecha, e.fecha) as fecha, " +
			"c.nombre as categoria, e.descripcion as descripcion " +
			"FROM movimiento m " +
			"LEFT JOIN ingreso i ON lower(m.tipotransferencia) = 'ingreso' AND m.idtransferencia = i.id " +
			"LEFT JOIN egreso e ON lower(m.tipotransferencia) = 'egreso' AND m.idtransferencia = e.id " +
			"LEFT JOIN categoria c ON e.idcat = c.id " +
			"WHERE e.idcat = :cat " +
			"ORDER BY fecha DESC",
			nativeQuery = true)
	List<Object[]> fetchByCategoriaNative(@Param("cat") int categoria);

}