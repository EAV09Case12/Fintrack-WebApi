package com.example.fintrack_webapi.application.dto.queries;

import java.util.Map;

public record PresupuestoResponseDTO(
    String fecha,
    double montoTotal,
    Map<String, Double> montosPorCategoria
) {}