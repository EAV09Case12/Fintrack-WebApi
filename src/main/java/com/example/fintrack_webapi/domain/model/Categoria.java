package com.example.fintrack_webapi.domain.model;

import lombok.Getter;

@Getter
public enum Categoria {
    SERVICIOS(1),
    ENTRETENIMIENTO(2),
    TRANSPORTE(3),
    ALIMENTACION(4),
    SALUD(5),
    DEUDAS(6);

    private final int codigo;

    Categoria(int codigo) {
        this.codigo = codigo;
    }
}
