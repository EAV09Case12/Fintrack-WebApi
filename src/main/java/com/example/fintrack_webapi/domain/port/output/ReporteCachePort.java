package com.example.fintrack_webapi.domain.port.output;

public interface ReporteCachePort {

    byte[] obtenerReporte(
            String requestId
    );
}