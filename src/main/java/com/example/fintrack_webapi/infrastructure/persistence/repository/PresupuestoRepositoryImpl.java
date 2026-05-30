package com.example.fintrack_webapi.infrastructure.persistence.repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Repository;

import com.example.fintrack_webapi.domain.model.PresupuestoMensual;
import com.example.fintrack_webapi.domain.port.output.PresupuestoRepositoryPort;
import com.example.fintrack_webapi.infrastructure.dao.PresupuestoJpaRepository;
import com.example.fintrack_webapi.infrastructure.persistence.entity.PresupuestoEntity;
import com.example.fintrack_webapi.infrastructure.persistence.mapper.PresupuestoMapper;
import com.example.fintrack_webapi.infrastructure.security.SecurityUtils;

@Repository
public class PresupuestoRepositoryImpl
        implements PresupuestoRepositoryPort {

    private final PresupuestoJpaRepository jpaRepository;

    public PresupuestoRepositoryImpl(
            PresupuestoJpaRepository jpaRepository
    ) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PresupuestoMensual guardar(
            PresupuestoMensual presupuesto
    ) {

        List<PresupuestoEntity> entities =
                PresupuestoMapper.toEntities(
                        presupuesto,
                        SecurityUtils.obtenerUsuarioAutenticado()
                );

        List<PresupuestoEntity> saved =
                jpaRepository.saveAll(Objects.requireNonNull(entities));

        return PresupuestoMapper.toDomain(saved);
    }

    @Override
    public PresupuestoMensual obtenerPorFecha(
            Date fecha
    ) {

        Calendar inicio =
                Calendar.getInstance();

        inicio.setTime(fecha);

        inicio.set(Calendar.DAY_OF_MONTH, 1);
        inicio.set(Calendar.HOUR_OF_DAY, 0);
        inicio.set(Calendar.MINUTE, 0);
        inicio.set(Calendar.SECOND, 0);
        inicio.set(Calendar.MILLISECOND, 0);

        Calendar fin =
                Calendar.getInstance();

        fin.setTime(inicio.getTime());

        fin.set(
                Calendar.DAY_OF_MONTH,
                fin.getActualMaximum(Calendar.DAY_OF_MONTH)
        );

        fin.set(Calendar.HOUR_OF_DAY, 23);
        fin.set(Calendar.MINUTE, 59);
        fin.set(Calendar.SECOND, 59);
        fin.set(Calendar.MILLISECOND, 999);

        List<PresupuestoEntity> rows =
                jpaRepository.findByFechaBetween(
                        inicio.getTime(),
                        fin.getTime()
                );

        return PresupuestoMapper.toDomain(rows);
    }
}