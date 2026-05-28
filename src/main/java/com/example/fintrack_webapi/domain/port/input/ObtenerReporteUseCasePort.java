package com.example.fintrack_webapi.domain.port.input;

public interface ObtenerReporteUseCasePort {

    byte[] obtenerReporte(
            String requestId
    );
}