package com.example.fintrack_webapi.adapters.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fintrack_webapi.application.dto.queries.BalanceDTO;
import com.example.fintrack_webapi.application.dto.queries.MovimientoDTO;
import com.example.fintrack_webapi.application.usecase.ConsultaUseCase;
import com.example.fintrack_webapi.infrastructure.dao.MovimientoJpaRepository;
import com.example.fintrack_webapi.domain.model.Categoria;
import com.example.fintrack_webapi.domain.port.input.BalanceUseCasePort;
import com.example.fintrack_webapi.domain.exception.BadRequestException;

import java.util.List;

@RestController
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
@Tag(name= "Consultas", description = "Endpoints de consulta de información")
public class HistorialController {

    private final ConsultaUseCase consultaUseCase;
    private final MovimientoJpaRepository movimientoRepository;
    private final BalanceUseCasePort balanceUseCase;

    @GetMapping("/historial")
    @Operation(summary = "Consultar historial", description = "Consultar el historial de transacciones")
    public ResponseEntity<List<MovimientoDTO>> consultarHistorial() {
        
        List<Object[]> rows = movimientoRepository.fetchHistorialNative();
        List<MovimientoDTO> resultado = rows.stream().map(row -> {
            Long id = row[0] == null ? null : ((Number) row[0]).longValue();
            String tipo = row[1] == null ? null : row[1].toString();
            double monto = row[2] == null ? 0.0 : ((Number) row[2]).doubleValue();
            String fecha = row[3] == null ? null : row[3].toString();
            String categoria = row[4] == null ? null : row[4].toString();
            String descripcion = row[5] == null ? null : row[5].toString();
            return new MovimientoDTO(id, tipo, monto, fecha, categoria, descripcion);
        }).toList();

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/ultimos")
    @Operation(summary = "Consultar últimos movimientos", description = "Consultar los últimos movimientos")
    public ResponseEntity<List<MovimientoDTO>> consultarUltimosMovimientos(@RequestParam int cantidad) {
        if (cantidad < 1 || cantidad > 20) {
            throw new BadRequestException("Parámetro 'cantidad' debe estar entre 1 y 20");
        }

        List<MovimientoDTO> resultado = consultaUseCase.obtenerUltimosMovimientos(cantidad);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/por-categoria")
    @Operation(summary = "Consultar movimientos por categoría", description = "Consultar movimientos filtrados por categoría")
    public ResponseEntity<List<MovimientoDTO>> consultarPorCategoria(@RequestParam int codigoCategoria) {
        boolean existe = false;
        for (Categoria c : Categoria.values()) {
            if (c.getCodigo() == codigoCategoria) {
                existe = true;
                break;
            }
        }

        if (!existe) {
            throw new BadRequestException("Categoría inválida");
        }

        List<MovimientoDTO> resultado = consultaUseCase.obtenerPorCategoria(codigoCategoria);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/balance")
    @Operation(summary = "Consultar balance financiero", description = "Obtiene ingresos, gastos y balance")
    public ResponseEntity<BalanceDTO> consultarBalance() {
        return ResponseEntity.ok(balanceUseCase.obtenerBalance());
    }
}