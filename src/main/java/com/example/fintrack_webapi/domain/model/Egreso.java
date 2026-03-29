package com.example.fintrack_webapi.domain.model;

import java.util.Date;
import lombok.Getter;


@Getter
public class Egreso extends Transaccion {
    private Categoria categoria;
    private String descripcion;

    public Egreso(double monto, Date fecha, Categoria categoria, String descripcion) {
        super(monto, fecha);
        this.categoria = categoria;
        this.descripcion = descripcion;
    }
}
