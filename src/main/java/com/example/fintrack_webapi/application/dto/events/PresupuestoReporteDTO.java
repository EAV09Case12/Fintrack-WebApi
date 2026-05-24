package com.example.fintrack_webapi.application.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresupuestoReporteDTO {

    private String fecha;

    private String categoria;

    private double monto;
}