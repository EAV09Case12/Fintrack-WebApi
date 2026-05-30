package com.example.fintrack_webapi.application.usecase;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.fintrack_webapi.application.dto.events.ReporteMensualEvent;
import com.example.fintrack_webapi.domain.model.Categoria;
import com.example.fintrack_webapi.domain.model.Egreso;
import com.example.fintrack_webapi.domain.model.Ingreso;
import com.example.fintrack_webapi.domain.model.PresupuestoMensual;
import com.example.fintrack_webapi.domain.model.Transaccion;
import com.example.fintrack_webapi.domain.port.output.PresupuestoRepositoryPort;
import com.example.fintrack_webapi.domain.port.output.TransaccionRepositoryPort;
import com.example.fintrack_webapi.infrastructure.messaging.ReporteProducer;

@ExtendWith(MockitoExtension.class)
class ReporteUseCaseTest {

    @Mock
    private TransaccionRepositoryPort transaccionRepository;

    @Mock
    private PresupuestoRepositoryPort presupuestoRepository;

    @Mock
    private ReporteProducer reporteProducer;

    @InjectMocks
    private ReporteUseCase useCase;

    private static Date fecha(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, day);
        return calendar.getTime();
    }

    private static int currentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    private static int currentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    @Test
    void generaReporte() {
        int mes = currentMonth();
        int anio = currentYear();

        Transaccion ingresoFueraMes = new Ingreso(100.0, fecha(anio, mes == 1 ? 2 : mes - 1, 10));
        Transaccion egresoEnMes = new Egreso(250.0, fecha(anio, mes, 12), Categoria.ALIMENTACION, "mercado");
        Transaccion ingresoEnMes = new Ingreso(500.0, fecha(anio, mes, 15));

        when(transaccionRepository.obtenerHistorial("ana@test.com"))
                .thenReturn(List.of(ingresoFueraMes, egresoEnMes, ingresoEnMes));

        PresupuestoMensual presupuesto = new PresupuestoMensual(
                fecha(anio, mes, 1),
                1000.0,
                Map.of(Categoria.ALIMENTACION, 600.0, Categoria.SERVICIOS, 400.0)
        );
        when(presupuestoRepository.obtenerPorFecha(any(Date.class))).thenReturn(presupuesto);

        useCase.generarReporteMensual(mes, "req-1", "ana@test.com");

        ArgumentCaptor<ReporteMensualEvent> captor = ArgumentCaptor.forClass(ReporteMensualEvent.class);
        verify(reporteProducer).enviarReporteMensual(captor.capture());

        ReporteMensualEvent event = captor.getValue();
        assertNotNull(event);
        assertEquals("req-1", event.getRequestId());
        assertEquals("ana@test.com", event.getEmailUsuario());
        assertEquals(mes, event.getMes());
        assertEquals(anio, event.getAnio());
        assertEquals(1, event.getEgresos().size());
        assertEquals(2, event.getPresupuestos().size());
    }

    @Test
    void errorNoSePropaga() {
        when(transaccionRepository.obtenerHistorial("ana@test.com"))
                .thenThrow(new RuntimeException("boom"));

        useCase.generarReporteMensual(currentMonth(), "req-2", "ana@test.com");

        verifyNoInteractions(presupuestoRepository);
        verify(reporteProducer, never()).enviarReporteMensual(any(ReporteMensualEvent.class));
    }
}
