package com.example.fintrack_webapi.application.dto.queries;

public record EgresoResponseDTO(
        double monto,
        String fecha,
        String categoria,
        String descripcion
) {}
