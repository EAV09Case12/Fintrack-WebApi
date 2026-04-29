package com.example.fintrack_webapi.domain.port.output;

public interface BalanceRepositoryPort {

    double obtenerTotalIngresos(int mes, int anio);
    double obtenerTotalGastos(int mes, int anio);
}