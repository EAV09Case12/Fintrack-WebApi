package com.example.fintrack_webapi.infrastructure.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.fintrack_webapi.infrastructure.persistence.entity.MovimientoEntity;

public interface MovimientoJpaRepository extends JpaRepository<MovimientoEntity, Long> {

    List<MovimientoEntity> findAllByOrderByFechaDesc();

    List<MovimientoEntity> findByCategoria(int categoria);
}