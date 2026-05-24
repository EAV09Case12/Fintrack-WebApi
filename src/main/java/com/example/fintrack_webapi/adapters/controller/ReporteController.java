package com.example.fintrack_webapi.adapters.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.fintrack_webapi.application.dto.queries.ReporteResponseDTO;
import com.example.fintrack_webapi.domain.exception.BadRequestException;
import com.example.fintrack_webapi.domain.port.input.ReporteUseCasePort;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/reportes")
@Validated
@Tag(
    name = "Reportes",
    description = "Endpoints para generación de reportes financieros"
)
public class ReporteController {

    private final ReporteUseCasePort reporteUseCase;

    public ReporteController(
            ReporteUseCasePort reporteUseCase
    ) {
        this.reporteUseCase = reporteUseCase;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/mensual")
    @Operation(
        summary = "Generar reporte mensual",
        responses = {
            @ApiResponse(
                responseCode = "202",
                description = "Reporte enviado correctamente",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Mes inválido",
                content = @Content
            )
        }
    )
    public ResponseEntity<ReporteResponseDTO>
    generarReporteMensual(

            @RequestParam
            @Min(1)
            @Max(12)
            int mes
    ) {

        if (mes < 1 || mes > 12) {

            throw new BadRequestException(
                    "El mes debe estar entre 1 y 12"
            );
        }

        String requestId =
                UUID.randomUUID().toString();

        String emailUsuario =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        reporteUseCase.generarReporteMensual(
                mes,
                requestId,
                emailUsuario
        );

        ReporteResponseDTO response =
                new ReporteResponseDTO(
                        "El reporte se está generando",
                        requestId
                );

        return ResponseEntity
                .accepted()
                .body(response);
    }
}