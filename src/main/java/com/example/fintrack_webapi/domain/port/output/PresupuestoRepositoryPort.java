package com.example.fintrack_webapi.domain.port.output;

import com.example.fintrack_webapi.domain.model.PresupuestoMensual;

import java.util.Date;

public interface PresupuestoRepositoryPort {

    PresupuestoMensual guardar(PresupuestoMensual presupuesto);

    PresupuestoMensual obtenerPorFecha(Date fecha);

}