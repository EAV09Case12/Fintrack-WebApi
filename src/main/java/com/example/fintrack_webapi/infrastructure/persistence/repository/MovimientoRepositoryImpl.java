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
import com.example.fintrack_webapi.infrastructure.persistence.entity.MovimientoEntity;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Comparator;


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

    @Override
    public Ingreso guardarIngreso(Ingreso ingreso) {

        var entity = TransaccionMapper.toEntityIngreso(ingreso);

        var saved = ingresoRepository.save(entity);

        return TransaccionMapper.toDomain(saved);
    }

    @Override
    public Egreso guardarEgreso(Egreso egreso) {

        var entity = TransaccionMapper.toEntityEgreso(egreso);

        var saved = egresoRepository.save(entity);
        
        return TransaccionMapper.toDomain(saved);
    }


    @Override
    public List<Transaccion> obtenerHistorial() {
        List<Transaccion> results = new ArrayList<>();

        List<MovimientoEntity> movimientos = movimientoRepository.findAll();

        for (MovimientoEntity m : movimientos) {
            String tipo = m.getTipoTransferencia();
            Integer id = m.getIdTransferencia();

            if (tipo == null || id == null) continue;

            if ("ingreso".equalsIgnoreCase(tipo)) {
                Optional.ofNullable(ingresoRepository.findById(id.longValue()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(TransaccionMapper::toDomain)
                        .ifPresent(t -> results.add((Transaccion) t));
            } else if ("egreso".equalsIgnoreCase(tipo)) {
                Optional.ofNullable(egresoRepository.findById(id.longValue()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(TransaccionMapper::toDomain)
                        .ifPresent(t -> results.add((Transaccion) t));
            }
        }

        results.sort(Comparator.comparing(Transaccion::getFecha).reversed());
        return results;
    }

    @Override
    public List<Transaccion> obtenerUltimosMovimientos(int cantidad) {
        List<Transaccion> all = obtenerHistorial();
        return all.stream().limit(cantidad).collect(Collectors.toList());
    }

    @Override
    public List<Transaccion> obtenerPorCategoria(int codigoCategoria) {
        List<Transaccion> results = new ArrayList<>();

        List<MovimientoEntity> movimientos = movimientoRepository.findAll();

        for (MovimientoEntity m : movimientos) {
            if (m.getTipoTransferencia() == null) continue;
            if (!"egreso".equalsIgnoreCase(m.getTipoTransferencia())) continue;

            Integer id = m.getIdTransferencia();
            if (id == null) continue;

            Optional.ofNullable(egresoRepository.findById(id.longValue()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(e -> e.getIdcat() == codigoCategoria)
                    .map(TransaccionMapper::toDomain)
                    .ifPresent(t -> results.add((Transaccion) t));
        }

        results.sort(Comparator.comparing(Transaccion::getFecha).reversed());
        return results;
    }

}