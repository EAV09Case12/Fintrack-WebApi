package com.example.fintrack_webapi.application.dto.queries;

public record BalanceDTO(
    double totalIngresos,
    double totalGastos,
    double balance,
    double porcentajeGastos,
    double porcentajeAhorro
) {}