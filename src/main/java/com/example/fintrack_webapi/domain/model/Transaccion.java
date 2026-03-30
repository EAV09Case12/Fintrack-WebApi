package com.example.fintrack_webapi.domain.model;

import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Transaccion {
    private double monto;
    private Date fecha;
}
