package com.example.fintrack_webapi.domain.model;

import lombok.Getter;
import java.util.Date;

@Getter
public class Ingreso extends Transaccion {
    public Ingreso(double monto, Date fecha) {
        super(monto, fecha);
    }
}
