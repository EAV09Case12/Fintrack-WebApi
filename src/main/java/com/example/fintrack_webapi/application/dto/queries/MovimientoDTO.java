package com.example.fintrack_webapi.application.dto.queries;

public record MovimientoDTO(
        String tipo,
        double monto,
        String fecha
) {}
