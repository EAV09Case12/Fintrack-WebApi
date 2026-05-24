package com.example.fintrack_webapi.domain.port.input;

public interface ReporteUseCasePort {

    void generarReporteMensual(
            int mes,
            String requestId,
            String emailUsuario
    );
}