package com.example.fintrack_webapi.application.usecase;

import org.springframework.stereotype.Service;

import com.example.fintrack_webapi.domain.exception.BadRequestException;

import com.example.fintrack_webapi.domain.port.input
        .ObtenerReporteUseCasePort;

import com.example.fintrack_webapi.domain.port.output
        .ReporteCachePort;

@Service
public class ObtenerReporteUseCase
        implements ObtenerReporteUseCasePort {

    private final ReporteCachePort
            reporteCachePort;

    public ObtenerReporteUseCase(
            ReporteCachePort reporteCachePort
    ) {

        this.reporteCachePort =
                reporteCachePort;
    }

    @Override
    public byte[] obtenerReporte(
            String requestId
    ) {

        byte[] pdf =
                reporteCachePort
                        .obtenerReporte(
                                requestId
                        );

        if (pdf == null) {

            throw new BadRequestException(
                    "Reporte no encontrado"
            );
        }

        return pdf;
    }
}