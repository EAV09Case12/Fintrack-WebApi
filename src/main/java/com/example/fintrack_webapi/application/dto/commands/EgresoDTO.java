package com.example.fintrack_webapi.application.dto.commands;



public record EgresoDTO(double monto, String fecha 
    /* ejemplo: "2026-01-01" */, int codigoCategoria, String descripcion) {

}
