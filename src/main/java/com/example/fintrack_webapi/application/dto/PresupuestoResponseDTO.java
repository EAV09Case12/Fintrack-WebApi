package com.example.fintrack_webapi.application.dto;

import java.util.Map;

public record PresupuestoResponseDTO(
    String fecha,
    double montoTotal,
    Map<String, Double> montosPorCategoria
) {}