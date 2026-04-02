package com.example.fintrack_webapi.infrastructure.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.fintrack_webapi.infrastructure.persistence.entity.EgresoEntity;

public interface EgresoJpaRepository extends JpaRepository<EgresoEntity, Long> {
}