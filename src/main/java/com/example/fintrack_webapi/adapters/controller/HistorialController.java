package com.example.fintrack_webapi.adapters.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fintrack_webapi.application.dto.queries.BalanceDTO;
import com.example.fintrack_webapi.application.dto.queries.MovimientoDTO;
import com.example.fintrack_webapi.application.usecase.ConsultaUseCase;

import org.springframework.security.access.prepost.PreAuthorize;

import com.example.fintrack_webapi.domain.model.Categoria;
import com.example.fintrack_webapi.domain.port.input.BalanceUseCasePort;
import com.example.fintrack_webapi.domain.exception.BadRequestException;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
@Tag(name= "Consultas", description = "Endpoints de consulta de información")
public class HistorialController {

    private final ConsultaUseCase consultaUseCase;
    private final BalanceUseCasePort balanceUseCase;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/historial")
    @Operation(
        summary = "Consultar historial",
        responses = {
            @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente", content = @Content),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
        }
    )
    public ResponseEntity<List<MovimientoDTO>> consultarHistorial() {

        List<MovimientoDTO> resultado =
                consultaUseCase.obtenerHistorial();

        return ResponseEntity.ok(resultado);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/ultimos")
    @Operation(
        summary = "Consultar últimos movimientos",
        responses = {
            @ApiResponse(responseCode = "200", description = "Movimientos obtenidos correctamente", content = @Content),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
        }
    )
    public ResponseEntity<List<MovimientoDTO>> consultarUltimosMovimientos(@RequestParam int cantidad) {

        if (cantidad < 1 || cantidad > 20) {
            throw new BadRequestException("Parámetro 'cantidad' debe estar entre 1 y 20");
        }

        List<MovimientoDTO> resultado =
                consultaUseCase.obtenerUltimosMovimientos(cantidad);

        return ResponseEntity.ok(resultado);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/por-categoria")
    @Operation(
        summary = "Consultar movimientos por categoría",
        responses = {
            @ApiResponse(responseCode = "200", description = "Movimientos filtrados correctamente", content = @Content),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
        }
    )
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

        List<MovimientoDTO> resultado =
                consultaUseCase.obtenerPorCategoria(codigoCategoria);

        return ResponseEntity.ok(resultado);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/balance")
    @Operation(
        summary = "Consultar balance financiero",
        responses = {
            @ApiResponse(responseCode = "200", description = "Balance obtenido correctamente", content = @Content),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
        }
    )
    public ResponseEntity<BalanceDTO> consultarBalance(
            @RequestParam(required = false) String fecha) {

        return ResponseEntity.ok(
                balanceUseCase.obtenerBalance(fecha)
        );
    }
}