package com.example.fintrack_webapi.infrastructure.persistence.repository;

import java.util.stream.Collectors;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.fintrack_webapi.domain.model.Egreso;
import com.example.fintrack_webapi.domain.model.Ingreso;
import com.example.fintrack_webapi.domain.model.Transaccion;
import com.example.fintrack_webapi.domain.port.output.TransaccionRepositoryPort;

import com.example.fintrack_webapi.infrastructure.persistence.mapper.TransaccionMapper;

import com.example.fintrack_webapi.infrastructure.dao.IngresoJpaRepository;
import com.example.fintrack_webapi.infrastructure.dao.EgresoJpaRepository;
import com.example.fintrack_webapi.infrastructure.dao.MovimientoJpaRepository;

@Repository
public class MovimientoRepositoryImpl implements TransaccionRepositoryPort {

    private final IngresoJpaRepository ingresoRepository;
    private final EgresoJpaRepository egresoRepository;
    private final MovimientoJpaRepository movimientoRepository;

    public MovimientoRepositoryImpl(
            IngresoJpaRepository ingresoRepository,
            EgresoJpaRepository egresoRepository,
            MovimientoJpaRepository movimientoRepository) {

        this.ingresoRepository = ingresoRepository;
        this.egresoRepository = egresoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    // =========================
    // INGRESO
    // =========================
    @Override
    public Ingreso guardarIngreso(Ingreso ingreso) {

        var entity = TransaccionMapper.toEntityIngreso(ingreso);

        var saved = ingresoRepository.save(entity);

        return TransaccionMapper.toDomain(saved);
    }

    // =========================
    // EGRESO
    // =========================
    @Override
    public Egreso guardarEgreso(Egreso egreso) {

        var entity = TransaccionMapper.toEntityEgreso(egreso);

        var saved = egresoRepository.save(entity);

        return TransaccionMapper.toDomain(saved);
    }

    // =========================
    // HISTORIAL
    // =========================
    @Override
    public List<Transaccion> obtenerHistorial() {

        return movimientoRepository.findAllByOrderByFechaDesc()
                .stream()
                .map(TransaccionMapper::toDomain)
                .collect(Collectors.toList());
    }

    // =========================
    // ÚLTIMOS MOVIMIENTOS
    // =========================
    @Override
    public List<Transaccion> obtenerUltimosMovimientos(int cantidad) {

        return movimientoRepository.findAllByOrderByFechaDesc()
                .stream()
                .limit(cantidad)
                .map(TransaccionMapper::toDomain)
                .collect(Collectors.toList());
    }

    // =========================
    // POR CATEGORÍA
    // =========================
    @Override
    public List<Transaccion> obtenerPorCategoria(int codigoCategoria) {

        return movimientoRepository.findByCategoria(codigoCategoria)
                .stream()
                .map(TransaccionMapper::toDomain)
                .collect(Collectors.toList());
    }
}