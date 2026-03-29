package com.example.fintrack_webapi.application.dto;

import java.util.Map;

public record PresupuestoDTO(
    double montoTotal,
    String fecha,
    Map<Integer, Double> porcentajes
) {}
