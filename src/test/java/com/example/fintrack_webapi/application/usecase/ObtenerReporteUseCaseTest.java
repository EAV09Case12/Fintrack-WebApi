package com.example.fintrack_webapi.application.usecase;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.fintrack_webapi.domain.exception.BadRequestException;
import com.example.fintrack_webapi.domain.port.output.ReporteCachePort;

@ExtendWith(MockitoExtension.class)
class ObtenerReporteUseCaseTest {

    @Mock
    private ReporteCachePort reporteCachePort;

    @InjectMocks
    private ObtenerReporteUseCase useCase;

    @Test
    void obtenerReporte() {
        byte[] pdf = new byte[] {1, 2, 3};
        when(reporteCachePort.obtenerReporte("req-1")).thenReturn(pdf);

        byte[] resultado = useCase.obtenerReporte("req-1");

        assertArrayEquals(pdf, resultado);
        verify(reporteCachePort).obtenerReporte("req-1");
    }

    @Test
    void reporteNoEncontrado() {
        when(reporteCachePort.obtenerReporte("req-2")).thenReturn(null);

        assertThrows(BadRequestException.class, () -> useCase.obtenerReporte("req-2"));
        verify(reporteCachePort).obtenerReporte("req-2");
    }
}
