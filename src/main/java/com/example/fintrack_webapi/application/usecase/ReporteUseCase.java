package com.example.fintrack_webapi.application.usecase;

import com.example.fintrack_webapi.application.dto.events.MovimientoReporteDTO;
import com.example.fintrack_webapi.application.dto.events.PresupuestoReporteDTO;
import com.example.fintrack_webapi.application.dto.events.ReporteMensualEvent;

import com.example.fintrack_webapi.domain.model.Egreso;
import com.example.fintrack_webapi.domain.model.PresupuestoMensual;
import com.example.fintrack_webapi.domain.model.Transaccion;

import com.example.fintrack_webapi.domain.port.input.ReporteUseCasePort;

import com.example.fintrack_webapi.domain.port.output.PresupuestoRepositoryPort;
import com.example.fintrack_webapi.domain.port.output.TransaccionRepositoryPort;

import com.example.fintrack_webapi.infrastructure.messaging.ReporteProducer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReporteUseCase
        implements ReporteUseCasePort {

    private final TransaccionRepositoryPort
            transaccionRepository;

    private final PresupuestoRepositoryPort
            presupuestoRepository;

    private final ReporteProducer
            reporteProducer;

    public ReporteUseCase(
            TransaccionRepositoryPort transaccionRepository,
            PresupuestoRepositoryPort presupuestoRepository,
            ReporteProducer reporteProducer
    ) {

        this.transaccionRepository =
                transaccionRepository;

        this.presupuestoRepository =
                presupuestoRepository;

        this.reporteProducer =
                reporteProducer;
    }

    @Async
    @Override
    public void generarReporteMensual(
            int mes,
            String requestId,
            String emailUsuario
    ) {

        try {

            log.info(
                    "Generando reporte mensual. mes={}, requestId={}, usuario={}",
                    mes,
                    requestId,
                    emailUsuario
            );

            int anioActual =
                    Calendar.getInstance()
                            .get(Calendar.YEAR);

            List<Transaccion> movimientos =
                transaccionRepository.obtenerHistorial(
                        emailUsuario
                );

            movimientos = movimientos.stream()
                    .filter(t -> {

                        Calendar cal =
                                Calendar.getInstance();

                        cal.setTime(t.getFecha());

                        int mesTransaccion =
                                cal.get(Calendar.MONTH) + 1;

                        int anioTransaccion =
                                cal.get(Calendar.YEAR);

                        return mesTransaccion == mes
                                && anioTransaccion == anioActual;
                    })
                    .toList();

            List<MovimientoReporteDTO> egresos =
                    movimientos.stream()
                            .filter(t -> t instanceof Egreso)
                            .map(t -> {

                                Egreso e = (Egreso) t;

                                return MovimientoReporteDTO
                                        .builder()
                                        .id(null)
                                        .monto(e.getMonto())
                                        .fecha(formatearFecha(e.getFecha()))
                                        .categoria(
                                                e.getCategoria().name()
                                        )
                                        .descripcion(
                                                e.getDescripcion()
                                        )
                                        .emailUsuario(
                                                emailUsuario
                                        )
                                        .build();
                            })
                            .collect(Collectors.toList());

            Calendar fechaPresupuesto =
                    Calendar.getInstance();

            fechaPresupuesto.set(
                    Calendar.YEAR,
                    anioActual
            );

            fechaPresupuesto.set(
                    Calendar.MONTH,
                    mes - 1
            );

            fechaPresupuesto.set(
                    Calendar.DAY_OF_MONTH,
                    1
            );

            Date fechaBusqueda =
                    fechaPresupuesto.getTime();

            PresupuestoMensual presupuesto =
                    presupuestoRepository.obtenerPorFecha(
                            fechaBusqueda
                    );

            List<PresupuestoReporteDTO> presupuestos =
                    presupuesto
                            .obtenerDistribucion()
                            .entrySet()
                            .stream()
                            .map(entry ->

                                    PresupuestoReporteDTO
                                            .builder()
                                            .fecha(
                                                    formatearFecha(
                                                            presupuesto.getFecha()
                                                    )
                                            )
                                            .categoria(
                                                    entry.getKey().name()
                                            )
                                            .monto(
                                                    entry.getValue()
                                            )
                                            .build()

                            )
                            .toList();

            ReporteMensualEvent event =
                    ReporteMensualEvent
                            .builder()
                            .requestId(requestId)
                            .emailUsuario(emailUsuario)
                            .mes(mes)
                            .anio(anioActual)
                            .egresos(egresos)
                            .presupuestos(presupuestos)
                            .build();

            log.info(
                    "EVENTO ENVIADO A RABBITMQ: {}",
                    event
            );

            reporteProducer.enviarReporteMensual(
                    event
            );

            log.info(
                    "Reporte enviado correctamente. requestId={}",
                    requestId
            );

        } catch (Exception e) {

            log.error(
                    "ERROR GENERANDO REPORTE: ",
                    e
            );
        }
    }

    private String formatearFecha(Date fecha) {

        return new SimpleDateFormat(
                "yyyy-MM-dd"
        ).format(fecha);
    }
}