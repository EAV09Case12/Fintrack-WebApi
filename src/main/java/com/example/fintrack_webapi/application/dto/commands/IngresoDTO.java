package com.example.fintrack_webapi.application.dto.commands;

import java.util.Map;

public record IngresoDTO(
    double monto,
    String fecha,
    Map<Integer, Double> porcentajes
) {}
