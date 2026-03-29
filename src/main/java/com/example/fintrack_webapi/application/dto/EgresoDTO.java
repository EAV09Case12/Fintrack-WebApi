package com.example.fintrack_webapi.application.dto;

import com.example.fintrack_webapi.domain.model.Categoria;


public record EgresoDTO(double monto, String fecha /* ejemplo: "2026-01-01" */, Categoria categoria, String descripcion) {

}
