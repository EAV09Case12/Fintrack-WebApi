package com.example.fintrack_webapi.application.usecase;

import com.example.fintrack_webapi.domain.model.PresupuestoMensual;
import com.example.fintrack_webapi.domain.model.Transaccion;
import com.example.fintrack_webapi.domain.port.input.ReporteUseCasePort;
import com.example.fintrack_webapi.domain.port.output.PresupuestoRepositoryPort;
import com.example.fintrack_webapi.domain.port.output.TransaccionRepositoryPort;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ReporteUseCase implements ReporteUseCasePort {

    private final TransaccionRepositoryPort transaccionRepository;
    private final PresupuestoRepositoryPort presupuestoRepository;

    public ReporteUseCase(
            TransaccionRepositoryPort transaccionRepository,
            PresupuestoRepositoryPort presupuestoRepository
    ) {
        this.transaccionRepository = transaccionRepository;
        this.presupuestoRepository = presupuestoRepository;
    }

    @Async
    @Override
    public void generarReporteMensual(int mes, String requestId) {

        log.info(
                "Iniciando generación de reporte mensual. mes={}, requestId={}",
                mes,
                requestId
        );

        int anioActual = Calendar.getInstance().get(Calendar.YEAR);

        List<Transaccion> movimientos =
                transaccionRepository.obtenerHistorial();

        movimientos = movimientos.stream()
                .filter(t -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(t.getFecha());

                    int mesTransaccion = cal.get(Calendar.MONTH) + 1;
                    int anioTransaccion = cal.get(Calendar.YEAR);

                    return mesTransaccion == mes
                            && anioTransaccion == anioActual;
                })
                .toList();

        Calendar fechaPresupuesto = Calendar.getInstance();
        fechaPresupuesto.set(Calendar.YEAR, anioActual);
        fechaPresupuesto.set(Calendar.MONTH, mes - 1);
        fechaPresupuesto.set(Calendar.DAY_OF_MONTH, 1);

        Date fechaBusqueda = fechaPresupuesto.getTime();

        PresupuestoMensual presupuesto =
                presupuestoRepository.obtenerPorFecha(fechaBusqueda);

        log.info(
                "Reporte generado correctamente. requestId={}",
                requestId
        );
    }
}