package com.example.fintrack_webapi.application.dto.commands;
import java.util.Map;

public record PresupuestoDTO(
    String fecha,
    double montoTotal,
    Map<String, Double> montosPorCategoria
) {
}
