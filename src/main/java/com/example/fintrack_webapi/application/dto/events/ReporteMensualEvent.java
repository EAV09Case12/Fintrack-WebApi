package com.example.fintrack_webapi.application.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteMensualEvent implements Serializable {

    private String requestId;

    private String emailUsuario;

    private int mes;

    private int anio;

    private List<MovimientoReporteDTO> egresos;

    private List<PresupuestoReporteDTO> presupuestos;
}