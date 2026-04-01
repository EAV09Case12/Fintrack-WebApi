package com.example.fintrack_webapi.application.dto.queries;

public record MovimientoDTO(
        Long id,
        String tipo,
        double monto,
        String fecha,
        String categoria,
        String descripcion
) {}

