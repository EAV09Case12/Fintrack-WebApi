package com.example.fintrack_webapi.application.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresupuestoReporteDTO {

    private String fecha;

    private double montoTotal;

    // codigoCategoria -> monto
    private Map<Integer, Double> montosPorCategoria;
}