package com.example.fintrack_webapi.infrastructure.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.fintrack_webapi.infrastructure.persistence.entity.IngresoEntity;

public interface IngresoJpaRepository extends JpaRepository<IngresoEntity, Long> {
}
