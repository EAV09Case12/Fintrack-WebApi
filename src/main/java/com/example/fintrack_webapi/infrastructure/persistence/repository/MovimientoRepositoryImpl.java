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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


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

   @Transactional
   @Override
    public Ingreso guardarIngreso(Ingreso ingreso) {

        var entity = TransaccionMapper.toEntityIngreso(ingreso);

        var saved = ingresoRepository.save(entity);

        MovimientoEntity movimiento = new MovimientoEntity();

        movimiento.setTipoTransferencia("INGRESO");
        movimiento.setIdTransferencia(saved.getId());
        movimiento.setEmailUsuario(obtenerUsuarioAutenticado());
        movimiento.setFechaMovimiento(LocalDateTime.now());

        movimientoRepository.save(movimiento);

        return TransaccionMapper.toDomain(saved);
    }
    
    @Transactional
    @Override
    public Egreso guardarEgreso(Egreso egreso) {

        var entity = TransaccionMapper.toEntityEgreso(egreso);

        var saved = egresoRepository.save(entity);

        MovimientoEntity movimiento = new MovimientoEntity();

        movimiento.setTipoTransferencia("EGRESO");
        movimiento.setIdTransferencia(saved.getId());
        movimiento.setEmailUsuario(obtenerUsuarioAutenticado());
        movimiento.setFechaMovimiento(LocalDateTime.now());

        movimientoRepository.save(movimiento);

        return TransaccionMapper.toDomain(saved);
    }


    @Override
    public List<Transaccion> obtenerHistorial() {

        List<Transaccion> results = new ArrayList<>();

        List<MovimientoEntity> movimientos = movimientoRepository.findAll();

        for (MovimientoEntity m : movimientos) {

            String tipo = m.getTipoTransferencia();
            Long id = m.getIdTransferencia();

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

            if (!"egreso".equalsIgnoreCase(m.getTipoTransferencia())) {
                continue;
            }

            Long id = m.getIdTransferencia();

            if (id == null) {
                continue;
            }

            egresoRepository.findById(id)
                    .filter(e -> e.getIdcat() == codigoCategoria)
                    .map(TransaccionMapper::toDomain)
                    .ifPresent(results::add);
        }

        results.sort(Comparator.comparing(Transaccion::getFecha).reversed());

        return results;
    }

    private String obtenerUsuarioAutenticado() {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return auth.getName();
    }

}