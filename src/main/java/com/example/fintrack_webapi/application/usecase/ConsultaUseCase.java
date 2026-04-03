package com.example.fintrack_webapi.application.usecase;

import com.example.fintrack_webapi.application.dto.queries.MovimientoDTO;
import com.example.fintrack_webapi.domain.model.Egreso;
import com.example.fintrack_webapi.domain.model.Transaccion;
import com.example.fintrack_webapi.domain.port.input.ConsultaUseCasePort;
import com.example.fintrack_webapi.domain.port.output.TransaccionRepositoryPort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultaUseCase implements ConsultaUseCasePort {

    private final TransaccionRepositoryPort transaccionRepo;

    public ConsultaUseCase(TransaccionRepositoryPort transaccionRepo) {
        this.transaccionRepo = transaccionRepo;
    }

    @Override
    public List<MovimientoDTO> obtenerHistorial() {
        return transaccionRepo.obtenerHistorial()
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<MovimientoDTO> obtenerUltimosMovimientos(int cantidad) {
        return transaccionRepo.obtenerUltimosMovimientos(cantidad).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<MovimientoDTO> obtenerPorCategoria(int codigoCategoria) {
        return transaccionRepo.obtenerPorCategoria(codigoCategoria).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    // =========================
    // MÉTODOS PRIVADOS
    // =========================

    private MovimientoDTO mapToDTO(Transaccion t) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        String tipo = (t instanceof Egreso) ? "EGRESO" : "INGRESO";
        String categoria = null;
        String descripcion = null;

        if (t instanceof Egreso) {
            Egreso e = (Egreso) t;
            categoria = e.getCategoria() != null ? e.getCategoria().name() : null;
            descripcion = e.getDescripcion();
        }

        String fecha = t.getFecha() != null ? fmt.format(t.getFecha()) : null;

        return new MovimientoDTO(null, tipo, t.getMonto(), fecha, categoria, descripcion);
    }
}
