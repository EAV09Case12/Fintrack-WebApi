package com.example.fintrack_webapi.infrastructure.persistence.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.fintrack_webapi.domain.model.Egreso;
import com.example.fintrack_webapi.domain.model.Ingreso;
import com.example.fintrack_webapi.domain.model.Transaccion;
import com.example.fintrack_webapi.domain.port.output.TransaccionRepositoryPort;

import com.example.fintrack_webapi.infrastructure.dao.EgresoJpaRepository;
import com.example.fintrack_webapi.infrastructure.dao.IngresoJpaRepository;
import com.example.fintrack_webapi.infrastructure.dao.MovimientoJpaRepository;

import com.example.fintrack_webapi.infrastructure.persistence.entity.MovimientoEntity;
import com.example.fintrack_webapi.infrastructure.persistence.mapper.TransaccionMapper;

import com.example.fintrack_webapi.infrastructure.security.SecurityUtils;

@Repository
public class MovimientoRepositoryImpl
        implements TransaccionRepositoryPort {

    private final IngresoJpaRepository ingresoRepository;
    private final EgresoJpaRepository egresoRepository;
    private final MovimientoJpaRepository movimientoRepository;

    public MovimientoRepositoryImpl(
            IngresoJpaRepository ingresoRepository,
            EgresoJpaRepository egresoRepository,
            MovimientoJpaRepository movimientoRepository
    ) {

        this.ingresoRepository = ingresoRepository;
        this.egresoRepository = egresoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional
    @Override
    public Ingreso guardarIngreso(Ingreso ingreso) {

        var entity = TransaccionMapper.toEntityIngreso(
                ingreso,
                SecurityUtils.obtenerUsuarioAutenticado()
        );

        var saved = ingresoRepository.save(entity);

        return TransaccionMapper.toDomain(saved);
    }

    @Transactional
    @Override
    public Egreso guardarEgreso(Egreso egreso) {

        var entity = TransaccionMapper.toEntityEgreso(
                egreso,
                SecurityUtils.obtenerUsuarioAutenticado()
        );

        var saved = egresoRepository.save(entity);

        return TransaccionMapper.toDomain(saved);
    }

    @Override
    public List<Transaccion> obtenerHistorial() {

        return obtenerHistorial(
                SecurityUtils.obtenerUsuarioAutenticado()
        );
    }

    @Override
    public List<Transaccion> obtenerHistorial(
            String emailUsuario
    ) {

        List<Transaccion> results =
                new ArrayList<>();

        List<MovimientoEntity> movimientos =
                movimientoRepository.findByUserEmail(
                        emailUsuario
                );

        for (MovimientoEntity m : movimientos) {

            String tipo =
                    m.getTipoTransferencia();

            Long id =
                    m.getIdTransferencia();

            if (tipo == null || id == null) {
                continue;
            }

            if ("ingreso".equalsIgnoreCase(tipo)) {

                ingresoRepository.findById(id)
                        .map(TransaccionMapper::toDomain)
                        .ifPresent(results::add);

            } else if ("egreso".equalsIgnoreCase(tipo)) {

                egresoRepository.findById(id)
                        .map(TransaccionMapper::toDomain)
                        .ifPresent(results::add);
            }
        }

        results.sort(
                Comparator.comparing(
                        Transaccion::getFecha
                ).reversed()
        );

        return results;
    }

    @Override
    public List<Transaccion> obtenerUltimosMovimientos(
            int cantidad
    ) {

        List<Transaccion> all =
                obtenerHistorial();

        return all.stream()
                .limit(cantidad)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaccion> obtenerPorCategoria(
            int codigoCategoria
    ) {

        List<Transaccion> results =
                new ArrayList<>();

        List<MovimientoEntity> movimientos =
                movimientoRepository.findByUserEmail(
                        SecurityUtils.obtenerUsuarioAutenticado()
                );

        for (MovimientoEntity m : movimientos) {

            if (!"egreso".equalsIgnoreCase(
                    m.getTipoTransferencia()
            )) {
                continue;
            }

            Long id = m.getIdTransferencia();

            if (id == null) {
                continue;
            }

            egresoRepository.findById(id)
                    .filter(e ->
                            e.getIdcat() == codigoCategoria
                    )
                    .map(TransaccionMapper::toDomain)
                    .ifPresent(results::add);
        }

        results.sort(
                Comparator.comparing(
                        Transaccion::getFecha
                ).reversed()
        );

        return results;
    }
}