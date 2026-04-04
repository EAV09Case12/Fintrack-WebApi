package com.example.fintrack_webapi.adapters.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fintrack_webapi.application.dto.queries.MovimientoDTO;
import com.example.fintrack_webapi.application.usecase.ConsultaUseCase;

import java.util.List;

@RestController
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
@Tag(name= "Consultas", description = "Endpoints de consulta de información")
public class HistorialController {

    private final ConsultaUseCase consultaUseCase;

    @GetMapping("/historial")
    @Operation(summary = "Consultar historial", description = "Consultar el historial de transacciones")
    public ResponseEntity<List<MovimientoDTO>> consultarHistorial() {
        List<MovimientoDTO> resultado = consultaUseCase.obtenerHistorial();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/ultimos")
    @Operation(summary = "Consultar últimos movimientos", description = "Consultar los últimos movimientos")
    public ResponseEntity<List<MovimientoDTO>> consultarUltimosMovimientos(@RequestParam int cantidad) {
        List<MovimientoDTO> resultado = consultaUseCase.obtenerUltimosMovimientos(cantidad);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/por-categoria")
    @Operation(summary = "Consultar movimientos por categoría", description = "Consultar movimientos filtrados por categoría")
    public ResponseEntity<List<MovimientoDTO>> consultarPorCategoria(@RequestParam int codigoCategoria) {
        List<MovimientoDTO> resultado = consultaUseCase.obtenerPorCategoria(codigoCategoria);
        return ResponseEntity.ok(resultado);
    }

}