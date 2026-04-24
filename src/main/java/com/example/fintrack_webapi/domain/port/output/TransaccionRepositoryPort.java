package com.example.fintrack_webapi.domain.port.output;

import com.example.fintrack_webapi.domain.model.Egreso;
import com.example.fintrack_webapi.domain.model.Ingreso;
import com.example.fintrack_webapi.domain.model.Transaccion;
import java.util.List;

public interface TransaccionRepositoryPort {
    Ingreso guardarIngreso(Ingreso ingreso);

    Egreso guardarEgreso(Egreso egreso);

    List<Transaccion> obtenerHistorial();

    List<Transaccion> obtenerUltimosMovimientos(int cantidad);

    List<Transaccion> obtenerPorCategoria(int codigoCategoria);

    double[] obtenerBalanceMensual(int mes, int anio);
}
