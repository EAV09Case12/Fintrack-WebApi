package com.example.fintrack_webapi.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.fintrack_webapi.application.dto.commands.EgresoDTO;
import com.example.fintrack_webapi.domain.model.Categoria;
import com.example.fintrack_webapi.domain.model.Egreso;
import com.example.fintrack_webapi.domain.port.output.PresupuestoRepositoryPort;
import com.example.fintrack_webapi.domain.port.output.TransaccionRepositoryPort;

@ExtendWith(MockitoExtension.class)
class RegistrarEgresoUseCaseTest {

    @Mock
    private TransaccionRepositoryPort transaccionRepo;

    @Mock
    private PresupuestoRepositoryPort presupuestoRepo;

    @InjectMocks
    private TransaccionUseCase transaccionUseCase;

    // Camino Feliz - valida que los datos sean correctos al llegar al repositorio
    @Test
    void registrarEgreso_DatosValidos_DebePersistirEgresoConDatosCorrectos() {
        EgresoDTO dto = new EgresoDTO(50000.0, "2026-03-10", 4, "Mercado semanal");

        transaccionUseCase.registrarEgreso(dto);

        ArgumentCaptor<Egreso> captor = ArgumentCaptor.forClass(Egreso.class);
        verify(transaccionRepo).guardarEgreso(captor.capture());

        Egreso egresoGuardado = captor.getValue();
        assertEquals(50000.0, egresoGuardado.getMonto());
        assertEquals(Categoria.ALIMENTACION, egresoGuardado.getCategoria());
        assertEquals("Mercado semanal", egresoGuardado.getDescripcion());
        assertNotNull(egresoGuardado.getFecha());

        // El presupuesto nunca debe tocarse al registrar un egreso
        verifyNoInteractions(presupuestoRepo);
    }

    //VALIDACION DE MONTOS

    // Caso de error: monto en cero.
    // Comportamiento esperado: rechazar monto 0 por regla de negocio.
    // Comportamiento actual del codigo: se acepta y se persiste el egreso.
    // Observación: hoy se permite registrar un gasto sin valor, lo que puede afectar métricas del historial.
    @Test
    void egresoMontoCero() {
        EgresoDTO dto = new EgresoDTO(0.0, "2026-03-10", 4, "Compra en cero");

        // ahora la lógica exige monto > 0
        assertThrows(com.example.fintrack_webapi.domain.exception.BadRequestException.class,
                () -> transaccionUseCase.registrarEgreso(dto));
    }

    // Caso de error: monto negativo.
    // Comportamiento esperado: rechazar monto negativo por regla de negocio.
    // Comportamiento actual del codigo: se acepta y se persiste el egreso con valor negativo.
    // Observación: al aceptar negativos, el egreso puede alterar saldos en dirección contraria a lo esperado.
    @Test
    void egresoMontoNegativo() {
        EgresoDTO dto = new EgresoDTO(-1000.0, "2026-03-10", 4, "Ajuste negativo");

        assertThrows(com.example.fintrack_webapi.domain.exception.BadRequestException.class,
                () -> transaccionUseCase.registrarEgreso(dto));
    }

    // Validacion de fechas

    // verificamos que una fecha en formato incorrecto lanza una excepcion, ademas
    // de comprobar que los repositorios no son llamados
    @Test
    void registrarEgreso_FechaConFormatoSlash_DebefallarAntesDeGuardar() {
        EgresoDTO dto = new EgresoDTO(50000.0, "10/03/2026", 4, "Compra");

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> transaccionUseCase.registrarEgreso(dto));

        assertEquals("Formato de fecha inválido. Usa yyyy-MM-dd", ex.getMessage());
        verifyNoInteractions(transaccionRepo);
        verifyNoInteractions(presupuestoRepo);
    }

    // verificamos que un texto arbitrario como fecha lanza una excepcion, ademas de
    // comprobar que los repositorios no son llamados
    @Test
    void registrarEgreso_FechaComoTextoArbitrario_DebefallarAntesDeGuardar() {
        EgresoDTO dto = new EgresoDTO(50000.0, "texto-invalido-yo-digo-que-hoy-tengo-sueño", 4, "Compra");
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> transaccionUseCase.registrarEgreso(dto));

        assertEquals("Formato de fecha inválido. Usa yyyy-MM-dd", ex.getMessage());
        verifyNoInteractions(transaccionRepo);
        verifyNoInteractions(presupuestoRepo);
    }

    // verificamos que una fecha nula lanza una excepcion antes de intentar
    // cualquier operacion de persistencia.
    @Test
    void registrarEgreso_FechaNula_DebefallarAntesDeGuardar() {
        EgresoDTO dto = new EgresoDTO(50000.0, null, 4, "Compra");
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> transaccionUseCase.registrarEgreso(dto));

        assertEquals("Formato de fecha inválido. Usa yyyy-MM-dd", ex.getMessage());
        verifyNoInteractions(transaccionRepo);
        verifyNoInteractions(presupuestoRepo);
    }

    // verificamos que un string vacío como fecha lanza una excepcion.
    @Test
    void registrarEgreso_FechaVacia_DebefallarAntesDeGuardar() {
        EgresoDTO dto = new EgresoDTO(50000.0, "", 4, "Compra");
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> transaccionUseCase.registrarEgreso(dto));

        assertEquals("Formato de fecha inválido. Usa yyyy-MM-dd", ex.getMessage());
        verifyNoInteractions(transaccionRepo);
        verifyNoInteractions(presupuestoRepo);
    }

    /*
     * Documenta el comportamiento actual de SimpleDateFormat en modo lenient:
     * "2026-02-30" no lanza excepción, sino que se redondea silenciosamente
     * a "2026-03-02". Este test NO valida comportamiento deseable —
     * valida el comportamiento REAL del código para dejar evidencia del bug.
     * TODO: Definir, si dejare ste comportamiento o ajustar para validar fechas
     * imposibles
     */

    @Test
    void registrarEgreso_FechaImposible_SimpleDateFormatAjustaSilenciosamente_BugDocumentado() {
        EgresoDTO dto = new EgresoDTO(50000.0, "2026-02-30", 4, "Test lenient");

        // actualmente la fecha inválida es rechazada por la validación estricta
        assertThrows(com.example.fintrack_webapi.domain.exception.BadRequestException.class,
            () -> transaccionUseCase.registrarEgreso(dto));
    }

    // Validación de categorias - valores limites

    // Limite inferior 1 -> SERVICIOS
    @Test
    void registrarEgreso_CodigoCategoria1_LimiteInferiorValido_DebeMapearAServicios() {
        EgresoDTO dto = new EgresoDTO(80000.0, "2026-01-01", 1, "Servicio de luz");

        transaccionUseCase.registrarEgreso(dto);
        ArgumentCaptor<Egreso> captor = ArgumentCaptor.forClass(Egreso.class);
        verify(transaccionRepo).guardarEgreso(captor.capture());
        assertEquals(Categoria.SERVICIOS, captor.getValue().getCategoria());
    }

    // Limite superior 6 -> DEUDAS
    @Test
    void registrarEgreso_CodigoCategoria6_LimiteSuperiorValido_DebeMapearADeudas() {
        EgresoDTO dto = new EgresoDTO(200000.0, "2026-01-01", 6, "Cuota préstamo");
        transaccionUseCase.registrarEgreso(dto);

        ArgumentCaptor<Egreso> captor = ArgumentCaptor.forClass(Egreso.class);
        verify(transaccionRepo).guardarEgreso(captor.capture());
        assertEquals(Categoria.DEUDAS, captor.getValue().getCategoria());
    }

    // Inmediatamente fuera del límite inferior: código 0. Debe lanzar excepción
    // antes de cualquier persistencia.
    @Test
    void registrarEgreso_CodigoCategoria0_FueraLimiteInferior_DebefallarAntesDeGuardar() {
        EgresoDTO dto = new EgresoDTO(50000.0, "2026-01-01", 0, "Test límite inferior");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> transaccionUseCase.registrarEgreso(dto));

        assertEquals("Categoría inválida", ex.getMessage());
        verifyNoInteractions(transaccionRepo);
        verifyNoInteractions(presupuestoRepo);
    }

    // Inmediatamente fuera del límite superior: código 7. Debe lanzar excepción
    // antes de cualquier persistencia.
    @Test
    void registrarEgreso_CodigoCategoria7_FueraLimiteSuperior_DebefallarAntesDeGuardar() {
        EgresoDTO dto = new EgresoDTO(50000.0, "2026-01-01", 7, "Test límite superior");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> transaccionUseCase.registrarEgreso(dto));

        assertEquals("Categoría inválida", ex.getMessage());
        verifyNoInteractions(transaccionRepo);
        verifyNoInteractions(presupuestoRepo);
    }

    // Código de categoría negativo: completamente fuera del dominio válido.
    @Test
    void registrarEgreso_CodigoCategoriaNegativos_DebefallarAntesDeGuardar() {
        EgresoDTO dto = new EgresoDTO(50000.0, "2026-01-01", -1, "Test negativo");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> transaccionUseCase.registrarEgreso(dto));

        assertEquals("Categoría inválida", ex.getMessage());
        verifyNoInteractions(transaccionRepo);
        verifyNoInteractions(presupuestoRepo);
    }
}
