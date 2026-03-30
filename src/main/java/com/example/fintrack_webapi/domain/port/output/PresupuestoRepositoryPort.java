package com.example.fintrack_webapi.domain.port.output;

import com.example.fintrack_webapi.domain.model.PresupuestoMensual;

public interface PresupuestoRepositoryPort {
    PresupuestoMensual guardar(PresupuestoMensual presupuesto);
}
