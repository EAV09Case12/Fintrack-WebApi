package com.example.fintrack_webapi.application.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoReporteDTO {

    private Long id;

    private double monto;

    private String fecha;

    private String categoria;

    private String descripcion;

    private String emailUsuario;
}