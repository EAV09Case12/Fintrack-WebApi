package com.example.fintrack_webapi.application.usecase;

import com.example.fintrack_webapi.application.dto.queries.MovimientoDTO;
import com.example.fintrack_webapi.domain.model.Categoria;
import com.example.fintrack_webapi.domain.model.Egreso;
import com.example.fintrack_webapi.domain.model.Ingreso;
import com.example.fintrack_webapi.domain.port.output.TransaccionRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaUseCaseTest {

    @Mock
    private TransaccionRepositoryPort transaccionRepo;

    @InjectMocks
    private ConsultaUseCase consultaUseCase;

    // Lista mixta de 3 transacciones: [Egreso, Ingreso, Egreso].
    // Valida que el mapeo es correcto posición a posición: tipo, categoría y
    // descripción deben corresponder a cada objeto original, sin cruzarse.
    @Test
    void obtenerHistorial_ListaMixta_DebeMapearCadaElementoAlTipoCorrecto() {
        Date fecha = new Date();
        Egreso egreso1 = new Egreso(30000.0, fecha, Categoria.SERVICIOS, "Luz del mes");
        Ingreso ingreso = new Ingreso(1500000.0, fecha);
        Egreso egreso2 = new Egreso(50000.0, fecha, Categoria.ALIMENTACION, "Mercado semanal");

        when(transaccionRepo.obtenerHistorial()).thenReturn(List.of(egreso1, ingreso, egreso2));

        List<MovimientoDTO> resultado = consultaUseCase.obtenerHistorial();

        assertEquals(3, resultado.size());

        // Posición 0: primer egreso
        assertEquals("EGRESO", resultado.get(0).tipo());
        assertEquals("SERVICIOS", resultado.get(0).categoria());
        assertEquals("Luz del mes", resultado.get(0).descripcion());

        // Posición 1: ingreso — categoria y descripcion siempre null para ingresos
        assertEquals("INGRESO", resultado.get(1).tipo());
        assertNull(resultado.get(1).categoria());
        assertNull(resultado.get(1).descripcion());

        // Posición 2: segundo egreso
        assertEquals("EGRESO", resultado.get(2).tipo());
        assertEquals("ALIMENTACION", resultado.get(2).categoria());
        assertEquals("Mercado semanal", resultado.get(2).descripcion());
    }

    // El repositorio no tiene registros. El use case no debe lanzar excepción ni
    // retornar null; la lista resultado debe estar vacía.
    @Test
    void obtenerHistorial_HistorialVacio_DebeRetornarListaVacia() {
        when(transaccionRepo.obtenerHistorial()).thenReturn(List.of());

        List<MovimientoDTO> resultado = consultaUseCase.obtenerHistorial();
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(transaccionRepo, times(1)).obtenerHistorial();
    }

    // Verifica que SimpleDateFormat formatea la fecha al string "yyyy-MM-dd"
    // correcto. Si el formato cambia, este test lo detecta.
    @Test
    void obtenerHistorial_IngresoConFechaValida_FechaDebeFormatearseComoYYYY_MM_DD() {
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.MARCH, 15, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date fecha = cal.getTime();

        Ingreso ingreso = new Ingreso(1000000.0, fecha);
        when(transaccionRepo.obtenerHistorial()).thenReturn(List.of(ingreso));

        List<MovimientoDTO> resultado = consultaUseCase.obtenerHistorial();

        assertEquals("2026-03-15", resultado.get(0).fecha());
    }

    // Una transacción con fecha=null no debería lanzar NullPointerException.
    @Test
    void obtenerHistorial_TransaccionConFechaNula_FechaEnDTODebeSerNull() {
        Ingreso ingresoSinFecha = new Ingreso(1000000.0, null);
        when(transaccionRepo.obtenerHistorial()).thenReturn(List.of(ingresoSinFecha));

        List<MovimientoDTO> resultado = consultaUseCase.obtenerHistorial();

        assertNull(resultado.get(0).fecha());
    }

    // Un Egreso con categoria=null no debería lanzar NPE.
    @Test
    void obtenerHistorial_EgresoConCategoriaNull_CategoriaEnDTODebeSerNull() {
        Egreso egresoSinCategoria = new Egreso(50000.0, new Date(), null, "Sin categoría asignada");
        when(transaccionRepo.obtenerHistorial()).thenReturn(List.of(egresoSinCategoria));

        List<MovimientoDTO> resultado = consultaUseCase.obtenerHistorial();

        assertNull(resultado.get(0).categoria());
    }

    // NOTA: este método solo delega al repo sin lógica propia.
    // La assertion clave es verify(), que confirma que el use case pasa
    // el argumento intacto. Probar el valor de retorno solo probaría el mock.

    // verificamos el contenido
    @Test
    void obtenerUltimosMovimientos_CantidadPositiva_DebeDelgarAlRepoConElMismoValor() {
        Date fecha = new Date();
        Egreso e1 = new Egreso(10000.0, fecha, Categoria.SERVICIOS, "Agua");
        Ingreso i1 = new Ingreso(500000.0, fecha);
        Egreso e2 = new Egreso(20000.0, fecha, Categoria.TRANSPORTE, "Bus mensual");

        when(transaccionRepo.obtenerUltimosMovimientos(3)).thenReturn(List.of(e1, i1, e2));

        List<MovimientoDTO> resultado = consultaUseCase.obtenerUltimosMovimientos(3);

        verify(transaccionRepo).obtenerUltimosMovimientos(3);
        assertEquals("EGRESO", resultado.get(0).tipo());
        assertEquals("INGRESO", resultado.get(1).tipo());
        assertEquals("EGRESO", resultado.get(2).tipo());
    }

    // Pedir 0 movimientos es semánticamente ilógico.
    // Hallazgo esperado: el use case no valida la cantidad antes de delegar —
    // el 0 llega al repo sin ninguna comprobación.
    // TODO: Revisar y discutir si se debe validar la cantidad
    @Test
    void obtenerUltimosMovimientos_CantidadCero_DebeDelgarAlRepoSinValidar() {
        when(transaccionRepo.obtenerUltimosMovimientos(0)).thenReturn(List.of());

        consultaUseCase.obtenerUltimosMovimientos(0);

        verify(transaccionRepo).obtenerUltimosMovimientos(0);
    }

    // Pedir una cantidad negativa es inválido por definición.
    // Hallazgo esperado: el use case acepta -1 sin lanzar excepción ni validar.
    // TODO: Revisar y discutir si se debe validar la cantidad
    @Test
    void obtenerUltimosMovimientos_CantidadNegativa_DebeDelgarAlRepoSinValidar() {
        when(transaccionRepo.obtenerUltimosMovimientos(-1)).thenReturn(List.of());

        consultaUseCase.obtenerUltimosMovimientos(-1);
        verify(transaccionRepo).obtenerUltimosMovimientos(-1);
    }

    // Código 1 es el límite inferior válido del enum Categoria (SERVICIOS).
    // Valida que el use case delega con ese código y que el mapeo devuelve la
    // categoría correcta en el DTO.
    @Test
    void obtenerPorCategoria_CodigoValido1_DebeDelgarAlRepoYMapearResultado() {
        Egreso egreso = new Egreso(80000.0, new Date(), Categoria.SERVICIOS, "Agua potable");
        when(transaccionRepo.obtenerPorCategoria(1)).thenReturn(List.of(egreso));

        List<MovimientoDTO> resultado = consultaUseCase.obtenerPorCategoria(1);

        verify(transaccionRepo).obtenerPorCategoria(1);
        assertEquals(1, resultado.size());
        assertEquals("SERVICIOS", resultado.get(0).categoria());
    }

    // Código 6 es el límite superior válido del enum Categoria (DEUDAS).
    @Test
    void obtenerPorCategoria_CodigoValido6_LimiteSuperior_DebeDelgarAlRepoYMapearResultado() {
        Egreso egreso = new Egreso(200000.0, new Date(), Categoria.DEUDAS, "Cuota préstamo");
        when(transaccionRepo.obtenerPorCategoria(6)).thenReturn(List.of(egreso));

        List<MovimientoDTO> resultado = consultaUseCase.obtenerPorCategoria(6);

        verify(transaccionRepo).obtenerPorCategoria(6);
        assertEquals("DEUDAS", resultado.get(0).categoria());
    }

    // Código 0 está fuera del rango válido [1–6].
    // El use case no valida el código y lo delega tal cual.
    // TODO: Revisar y discutir si se debe validar el código
    @Test
    void obtenerPorCategoria_CodigoCero_DebeDelgarAlRepoSinValidar() {
        when(transaccionRepo.obtenerPorCategoria(0)).thenReturn(List.of());

        consultaUseCase.obtenerPorCategoria(0);

        verify(transaccionRepo).obtenerPorCategoria(0);
    }

    // Código 7 está inmediatamente por encima del límite superior válido.
    // fluye hasta el repo sin validación.
    // TODO: Revisar y discutir si se debe validar el código
    @Test
    void obtenerPorCategoria_CodigoSiete_FueraDeRango_DebeDelgarAlRepoSinValidar() {
        when(transaccionRepo.obtenerPorCategoria(7)).thenReturn(List.of());

        consultaUseCase.obtenerPorCategoria(7);
        verify(transaccionRepo).obtenerPorCategoria(7);
    }

    // Código negativo: completamente fuera del dominio válido.
    // El use case acepta -1 sin ninguna validación.
    // TODO: Revisar y discutir si se debe validar el código
    @Test
    void obtenerPorCategoria_CodigoNegativo_DebeDelgarAlRepoSinValidar() {
        when(transaccionRepo.obtenerPorCategoria(-1)).thenReturn(List.of());
        consultaUseCase.obtenerPorCategoria(-1);
        verify(transaccionRepo).obtenerPorCategoria(-1);
    }

    // Cuando el repositorio no tiene registros para una categoría válida,
    // el resultado debe ser una lista vacía, nunca null.
    @Test
    void obtenerPorCategoria_RepoDevuelveListaVacia_DebeRetornarListaVaciaNoNull() {
        when(transaccionRepo.obtenerPorCategoria(4)).thenReturn(List.of());

        List<MovimientoDTO> resultado = consultaUseCase.obtenerPorCategoria(4);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
