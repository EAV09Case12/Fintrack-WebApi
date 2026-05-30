package com.example.fintrack_webapi.adapters.controller;

import java.util.UUID;

import org.springframework.core.io.ByteArrayResource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;

import com.example.fintrack_webapi.application.dto.queries.ReporteResponseDTO;

import com.example.fintrack_webapi.domain.exception.BadRequestException;

import com.example.fintrack_webapi.domain.port.input.ReporteUseCasePort;
import com.example.fintrack_webapi.domain.port.input.ObtenerReporteUseCasePort;

import com.example.fintrack_webapi.infrastructure.security.SecurityUtils;

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

    private final ObtenerReporteUseCasePort
            obtenerReporteUseCase;

    public ReporteController(
            ReporteUseCasePort reporteUseCase,
            ObtenerReporteUseCasePort obtenerReporteUseCase
    ) {

        this.reporteUseCase =
                reporteUseCase;

        this.obtenerReporteUseCase =
                obtenerReporteUseCase;
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
                SecurityUtils.obtenerUsuarioAutenticado();

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

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{requestId}")
    @Operation(
        summary = "Descargar reporte PDF",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "PDF encontrado"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Reporte no encontrado"
            )
        }
    )
    public ResponseEntity<ByteArrayResource>
    descargarReporte(
            @PathVariable
            String requestId
    ) {

        byte[] pdf =
                obtenerReporteUseCase
                        .obtenerReporte(
                                requestId
                        );

        ByteArrayResource resource =
                new ByteArrayResource(pdf);

        return ResponseEntity.ok()
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte-financiero.pdf"
                )
                .contentLength(pdf.length)
                .body(resource);
    }
}