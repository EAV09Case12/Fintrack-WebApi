package com.example.fintrack_webapi.application.usecase;

import com.example.fintrack_webapi.domain.model.Egreso;
import com.example.fintrack_webapi.domain.model.Transaccion;

import com.example.fintrack_webapi.application.dto.queries.MovimientoDTO;
import com.example.fintrack_webapi.application.dto.queries.EgresoResponseDTO;
import com.example.fintrack_webapi.domain.port.input.ConsultaUseCasePort;
import com.example.fintrack_webapi.domain.port.output.TransaccionRepositoryPort;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ConsultaUseCase implements ConsultaUseCasePort {

    private final TransaccionRepositoryPort transaccionRepo;

    public ConsultaUseCase(TransaccionRepositoryPort transaccionRepo) {
        this.transaccionRepo = transaccionRepo;
    }

    @Override
    public List<MovimientoDTO> obtenerUltimosMovimientos(int cantidad) {

        List<Transaccion> historial = transaccionRepo.obtenerHistorial();

        historial.sort(Comparator.comparing(Transaccion::getFecha).reversed());

        int limit = Math.min(cantidad, historial.size());

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        List<MovimientoDTO> resultado = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            Transaccion t = historial.get(i);
            String tipo = (t instanceof Egreso) ? "EGRESO" : "INGRESO";

            String fechaStr = t.getFecha() != null ? fmt.format(t.getFecha()) : null;

            resultado.add(new MovimientoDTO(tipo, t.getMonto(), fechaStr));
        }

        return resultado;
    }

    @Override
    public List<EgresoResponseDTO> obtenerPorCategoria(int codigoCategoria) {

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        return transaccionRepo.obtenerHistorial().stream()
                .filter(t -> t instanceof Egreso)
                .map(t -> (Egreso) t)
                .filter(e -> e.getCategoria() != null && e.getCategoria().getCodigo() == codigoCategoria)
                .sorted(Comparator.comparing(Transaccion::getFecha).reversed())
                .map(e -> new EgresoResponseDTO(
                        e.getMonto(),
                        e.getFecha() != null ? fmt.format(e.getFecha()) : null,
                        e.getCategoria() != null ? e.getCategoria().name() : null,
                        e.getDescripcion()
                ))
                .collect(Collectors.toList());
    }
}
