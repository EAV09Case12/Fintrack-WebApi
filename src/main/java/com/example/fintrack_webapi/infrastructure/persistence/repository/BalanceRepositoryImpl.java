package com.example.fintrack_webapi.infrastructure.persistence.repository;

import com.example.fintrack_webapi.domain.port.output.BalanceRepositoryPort;
import com.example.fintrack_webapi.infrastructure.dao.EgresoJpaRepository;
import com.example.fintrack_webapi.infrastructure.dao.IngresoJpaRepository;
import com.example.fintrack_webapi.infrastructure.security.SecurityUtils;

import org.springframework.stereotype.Repository;

@Repository
public class BalanceRepositoryImpl implements BalanceRepositoryPort {

    private final IngresoJpaRepository ingresoRepository;
    private final EgresoJpaRepository egresoRepository;

    public BalanceRepositoryImpl(
            IngresoJpaRepository ingresoRepository,
            EgresoJpaRepository egresoRepository
    ) {
        this.ingresoRepository = ingresoRepository;
        this.egresoRepository = egresoRepository;
    }

    @Override
    public double obtenerTotalIngresos(int mes, int anio) {

        return ingresoRepository
                .findByMesAndAnio(
                        SecurityUtils.obtenerUsuarioAutenticado(),
                        mes,
                        anio
                )
                .stream()
                .mapToDouble(i -> i.getMonto())
                .sum();
    }

    @Override
    public double obtenerTotalGastos(int mes, int anio) {

        return egresoRepository
                .findByMesAndAnio(
                        SecurityUtils.obtenerUsuarioAutenticado(),
                        mes,
                        anio
                )
                .stream()
                .mapToDouble(e -> e.getMonto())
                .sum();
    }
}