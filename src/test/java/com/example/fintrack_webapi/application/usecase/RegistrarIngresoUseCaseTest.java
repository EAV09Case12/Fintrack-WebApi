package com.example.fintrack_webapi.application.usecase;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.fintrack_webapi.application.dto.commands.IngresoDTO;
import com.example.fintrack_webapi.application.dto.queries.PresupuestoResponseDTO;
import com.example.fintrack_webapi.domain.exception.BadRequestException;
import com.example.fintrack_webapi.domain.model.Ingreso;
import com.example.fintrack_webapi.domain.model.PresupuestoMensual;
import com.example.fintrack_webapi.domain.port.output.PresupuestoRepositoryPort;
import com.example.fintrack_webapi.domain.port.output.TransaccionRepositoryPort;

@ExtendWith(MockitoExtension.class)
class RegistrarIngresoUseCaseTest {

    @Mock
    private TransaccionRepositoryPort transaccionRepo;

    @Mock
    private PresupuestoRepositoryPort presupuestoRepo;

    @InjectMocks
    private TransaccionUseCase transaccionUseCase;

    @BeforeEach
    void configurarMocks() {
        lenient().when(transaccionRepo.guardarIngreso(any(Ingreso.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(presupuestoRepo.guardar(any(PresupuestoMensual.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    // Helpers para reducir duplicación
    private IngresoDTO dto(double monto, String fecha, Map<Integer, Double> porcentajes) {
        return new IngresoDTO(monto, fecha, porcentajes);
    }

    private void assertFormatoFechaInvalido(Executable action) {
        RuntimeException ex = assertThrows(RuntimeException.class, action);
        assertEquals("Formato de fecha inválido. Usa yyyy-MM-dd", ex.getMessage());
    }

    // Camino Feliz - montos por categoria correctos, monto total correcto y ambos
    // repositorios invocados exactamente una vez
    @Test
    void registrarIngreso_DatosValidos_DebeCalcularDistribucionYPersistir() {
        Map<Integer, Double> porcentajes = Map.of(
                1, 30.0, // SERVICIOS -> 1.500.000 * 30% = 450.000
                4, 40.0, // ALIMENTACION -> 600.000
                3, 30.0 // TRANSPORTE -> 450.000
        );
        IngresoDTO dto = new IngresoDTO(1_500_000.0, "2026-04-01", porcentajes);

        PresupuestoResponseDTO response = transaccionUseCase.registrarIngreso(dto);

        // montoTotal correcto en la respuesta
        assertEquals(1_500_000.0, response.montoTotal());

        // montos por categoría calculados correctamente
        assertEquals(450_000.0, response.montosPorCategoria().get("SERVICIOS"));
        assertEquals(600_000.0, response.montosPorCategoria().get("ALIMENTACION"));
        assertEquals(450_000.0, response.montosPorCategoria().get("TRANSPORTE"));

        // ambos repositorios invocados exactamente una vez
        verify(transaccionRepo, times(1)).guardarIngreso(any(Ingreso.class));
        verify(presupuestoRepo, times(1)).guardar(any(PresupuestoMensual.class));
    }

    // Camino Feliz - el ingreso llega al repositorio con los datos correctos
    @Test
    void registrarIngreso_DatosValidos_DebeGuardarIngresoConDatosCorrectos() {
        Map<Integer, Double> porcentajes = Map.of(4, 100.0);
        IngresoDTO dto = new IngresoDTO(2_000_000.0, "2026-04-15", porcentajes);

        transaccionUseCase.registrarIngreso(dto);

        ArgumentCaptor<Ingreso> captor = ArgumentCaptor.forClass(Ingreso.class);
        verify(transaccionRepo).guardarIngreso(captor.capture());

        Ingreso ingresoGuardado = captor.getValue();
        assertEquals(2_000_000.0, ingresoGuardado.getMonto());
        assertNotNull(ingresoGuardado.getFecha());

        // Verificar que la fecha parseada corresponde al 15 de abril de 2026
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(ingresoGuardado.getFecha());
        assertEquals(2026, cal.get(java.util.Calendar.YEAR));
        assertEquals(3, cal.get(java.util.Calendar.MONTH));// APRIL = 3 (base 0)
        assertEquals(15, cal.get(java.util.Calendar.DAY_OF_MONTH));
    }

    // Camino Feliz - las claves del mapa son nombres del enum, no códigos numéricos
    @Test
    void registrarIngreso_DatosValidos_DebeMapearCategoriasComoNombreEnRespuesta() {
        Map<Integer, Double> porcentajes = Map.of(
                1, 20.0, // SERVICIOS
                6, 80.0// DEUDAS
        );
        IngresoDTO dto = new IngresoDTO(500_000.0, "2026-01-01", porcentajes);

        PresupuestoResponseDTO response = transaccionUseCase.registrarIngreso(dto);

        // las claves son nombres del enum, no códigos numéricos
        assertTrue(response.montosPorCategoria().containsKey("SERVICIOS"),
                "La clave debe ser 'SERVICIOS', no el código 1");
        assertTrue(response.montosPorCategoria().containsKey("DEUDAS"),
                "La clave debe ser 'DEUDAS', no el código 6");

        // montos calculados correctamente (500.000 * 20% = 100.000)
        assertEquals(100_000.0, response.montosPorCategoria().get("SERVICIOS"));
        assertEquals(400_000.0, response.montosPorCategoria().get("DEUDAS"));
    }

    //VALIDACION DE MONTOS

    // Caso de error: monto en cero.
    // Comportamiento esperado: rechazar monto 0 por regla de negocio.
    // Comportamiento actual del codigo: se acepta, se guarda ingreso y genera presupuesto con montos en 0.
    // Observación: hoy el sistema permite registrar un ingreso sin valor, lo cual puede distorsionar reportes.
    @Test
    void ingresoMontoCero() {
        IngresoDTO dto = dto(0.0, "2026-01-01", Map.of(1, 100.0));

        // La implementación actual exige monto > 0
        assertThrows(BadRequestException.class, () -> transaccionUseCase.registrarIngreso(dto));
    }

    // Caso de error: monto negativo.
    // Comportamiento esperado: rechazar monto negativo por regla de negocio.
    // Comportamiento actual del codigo: se acepta y persiste valores negativos.
    // Observación: hoy el sistema trata un ingreso negativo como válido, aunque funcionalmente se comporta como egreso.
    @Test
    void ingresoMontoNegativo() {
        IngresoDTO dto = dto(-500_000.0, "2026-01-01", Map.of(1, 100.0));
        assertThrows(BadRequestException.class, () -> transaccionUseCase.registrarIngreso(dto));
    }

    //VALIDACION DE PORCENTAJES

    // Caso de error: porcentajes vacios.
    // Comportamiento esperado: rechazar antes de persistir cualquier dato.
    // Comportamiento actual del codigo: primero guarda ingreso y luego falla al distribuir (suma != 100).
    // Observación: queda persistencia parcial; el ingreso existe en BD aunque el presupuesto falle.
    @Test
    void ingresoPorcentajesVaciosFallaDespuesDeGuardarIngreso() {
        IngresoDTO dto = dto(500_000.0, "2026-01-01", Map.of());

        // distribuir lanza IllegalArgumentException, la capa lo mapea a BadRequestException
        BadRequestException ex = assertThrows(BadRequestException.class,
            () -> transaccionUseCase.registrarIngreso(dto));

        assertTrue(ex.getMessage().startsWith("La suma de los porcentajes"));
        // la validación ocurre antes de persistir
        verifyNoInteractions(transaccionRepo);
        verifyNoInteractions(presupuestoRepo);
    }

    // Caso de error: porcentajes nulos.
    // Comportamiento esperado: validación controlada con mensaje claro.
    // Comportamiento actual del codigo: ocurre NullPointerException al convertir categorias.
    // Observación: falta validación de null en entrada; el error técnico llega tal cual sin mensaje de negocio.
    @Test
    void ingresoPorcentajesNullPropagaNpe() {
        IngresoDTO dto = dto(500_000.0, "2026-01-01", null);

        // convertirCategorias intenta iterar y propaga NPE; no debe persistir
        assertThrows(NullPointerException.class, () -> transaccionUseCase.registrarIngreso(dto));
        verifyNoInteractions(transaccionRepo);
        verifyNoInteractions(presupuestoRepo);
    }

    // Caso de error: suma de porcentajes distinta de 100.
    // Comportamiento esperado: rechazar la solicitud sin persistencia parcial.
    // Comportamiento actual del codigo: se guarda ingreso y luego falla en la regla de suma.
    // Observación: la validación llega tarde; primero se persiste ingreso y después se detecta el error.
    @Test
    void ingresoPorcentajesSumaDistintaFallaConPersistenciaParcial() {
        IngresoDTO dto = dto(500_000.0, "2026-01-01", Map.of(1, 40.0, 4, 40.0));

        BadRequestException ex = assertThrows(BadRequestException.class,
            () -> transaccionUseCase.registrarIngreso(dto));

        assertTrue(ex.getMessage().startsWith("La suma de los porcentajes"));
        verifyNoInteractions(transaccionRepo);
        verifyNoInteractions(presupuestoRepo);
    }

    // Validacion Fechas

    // Verifica que una fecha en formato slash lanza excepción y ningún repositorio
    // es invocado (fallo temprano).
    @Test
    void registrarIngreso_FechaConFormatoSlash_DebefallarAntesDeGuardar() {
        IngresoDTO dto = dto(500_000.0, "15/01/2026", Map.of(1, 100.0));
        assertFormatoFechaInvalido(() -> transaccionUseCase.registrarIngreso(dto));
        verifyNoInteractions(transaccionRepo);
        verifyNoInteractions(presupuestoRepo);
    }

    // Verifica que una fecha nula lanza excepción con mensaje correcto
    @Test
    void registrarIngreso_FechaNula_DebefallarAntesDeGuardar() {
        IngresoDTO dto = dto(500_000.0, null, Map.of(1, 100.0));
        assertFormatoFechaInvalido(() -> transaccionUseCase.registrarIngreso(dto));
        verifyNoInteractions(transaccionRepo);
        verifyNoInteractions(presupuestoRepo);
    }

    // Verifica que un string vacío como fecha lanza excepción.
    @Test
    void registrarIngreso_FechaVacia_DebefallarAntesDeGuardar() {
        IngresoDTO dto = dto(500_000.0, "", Map.of(1, 100.0));
        assertFormatoFechaInvalido(() -> transaccionUseCase.registrarIngreso(dto));
        verifyNoInteractions(transaccionRepo);
        verifyNoInteractions(presupuestoRepo);
    }

    // Documenta el comportamiento lenient de SimpleDateFormat:
    // "2026-02-30" no falla sino que se ajusta silenciosamente a 2026-03-02.
    // Este test documenta el BUG ACTUAL. TODO: Ajustar o conservar documentado,
    // segun se discuta con el equipo
    @Test
    void registrarIngreso_FechaImposible_SimpleDateFormatAjustaSilenciosamente_BugDocumentado() {
        IngresoDTO dto = dto(500_000.0, "2026-02-30", Map.of(1, 100.0));

        // la implementación actual usa parsing estricto y rechazará la fecha
        assertFormatoFechaInvalido(() -> transaccionUseCase.registrarIngreso(dto));
    }

    // Validación categorias - valores limites

    // Límite inferior válido: código 1 -> SERVICIOS.
    @Test
    void registrarIngreso_CodigoCategoria1_LimiteInferiorValido_DebeAceptarse() {
        Map<Integer, Double> porcentajes = Map.of(1, 100.0);
        IngresoDTO dto = new IngresoDTO(500_000.0, "2026-01-01", porcentajes);

        PresupuestoResponseDTO response = transaccionUseCase.registrarIngreso(dto);

        assertTrue(response.montosPorCategoria().containsKey("SERVICIOS"));
        assertEquals(500_000.0, response.montosPorCategoria().get("SERVICIOS"));
    }

    // Límite superior válido: código 6 -> DEUDAS.
    @Test
    void registrarIngreso_CodigoCategoria6_LimiteSuperiorValido_DebeAceptarse() {
        Map<Integer, Double> porcentajes = Map.of(6, 100.0);
        IngresoDTO dto = new IngresoDTO(500_000.0, "2026-01-01", porcentajes);

        PresupuestoResponseDTO response = transaccionUseCase.registrarIngreso(dto);

        assertTrue(response.montosPorCategoria().containsKey("DEUDAS"));
        assertEquals(500_000.0, response.montosPorCategoria().get("DEUDAS"));
    }

    // Inmediatamente fuera del límite inferior: código 0.
    // La excepción ocurre en convertirCategorias() — DESPUÉS de que
    // transaccionRepo.guardarIngreso() ya fue llamado (paso 3 del use case).
    @Test
    void registrarIngreso_CodigoCategoria0_FueraLimiteInferior_DebefallarAntesDePersistirPresupuesto() {
        IngresoDTO dto = dto(500_000.0, "2026-01-01", Map.of(0, 100.0));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> transaccionUseCase.registrarIngreso(dto));

        assertEquals("Categoría inválida", ex.getMessage());

        // la validación ocurre antes de persistir
        verifyNoInteractions(transaccionRepo);
        verifyNoInteractions(presupuestoRepo);
    }

    // Inmediatamente fuera del límite superior: código 7.
    // Mismo comportamiento que código 0: el ingreso ya fue persistido
    // cuando falla la conversión de categoría.
    @Test
    void registrarIngreso_CodigoCategoria7_FueraLimiteSuperior_DebefallarAntesDePersistirPresupuesto() {
        IngresoDTO dto = dto(500_000.0, "2026-01-01", Map.of(7, 100.0));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> transaccionUseCase.registrarIngreso(dto));

        assertEquals("Categoría inválida", ex.getMessage());
        verifyNoInteractions(transaccionRepo);
        verifyNoInteractions(presupuestoRepo);
    }

    // Código de categoría negativo: fuera del dominio válido.
    @Test
    void registrarIngreso_CodigoCategoriaNegativos_DebefallarAntesDePersistirPresupuesto() {
        IngresoDTO dto = dto(500_000.0, "2026-01-01", Map.of(-1, 100.0));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> transaccionUseCase.registrarIngreso(dto));

        assertEquals("Categoría inválida", ex.getMessage());
        verifyNoInteractions(transaccionRepo);
        verifyNoInteractions(presupuestoRepo);
    }
}