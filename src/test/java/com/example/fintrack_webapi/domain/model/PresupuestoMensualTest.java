package com.example.fintrack_webapi.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// Pruebas unitarias del presupuesto mensual, no se requieren moks dado que es un modelo de dominio puro sin dependencias externas.

class PresupuestoMensualTest {

    // Al construir un PresupuestoMensual desde un Ingreso, la fecha y el monto
    // deben ser los del ingreso. La distribución aún no existe.
    @Test
    void constructor_ConIngreso_DebeTomárFechaYMontoDelIngreso() {
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.MARCH, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date fecha = cal.getTime();
        Ingreso ingreso = new Ingreso(1_500_000.0, fecha);

        PresupuestoMensual presupuesto = new PresupuestoMensual(ingreso);

        assertEquals(fecha, presupuesto.getFecha());
        assertEquals(1_500_000.0, presupuesto.getMontoTotal());
        assertTrue(presupuesto.obtenerDistribucion().isEmpty());
    }

    // El constructor alternativo recibe fecha, monto y un mapa de montos ya
    // calculados. Los tres campos deben quedar precargados correctamente.
    @Test
    void constructor_ConFechaMontoYMapa_DebePrecargarDistribucion() {
        Date fecha = new Date();
        Map<Categoria, Double> montos = new HashMap<>();
        montos.put(Categoria.SERVICIOS, 300_000.0);
        montos.put(Categoria.ALIMENTACION, 700_000.0);

        PresupuestoMensual presupuesto = new PresupuestoMensual(fecha, 1_000_000.0, montos);

        assertEquals(fecha, presupuesto.getFecha());
        assertEquals(1_000_000.0, presupuesto.getMontoTotal());
        assertEquals(300_000.0, presupuesto.obtenerDistribucion().get(Categoria.SERVICIOS));
        assertEquals(700_000.0, presupuesto.obtenerDistribucion().get(Categoria.ALIMENTACION));
    }

    // La regla: Math.abs(suma - 100.0) > 0.1 lanza IllegalArgumentException.
    // Rango aceptado: (99.9, 100.1]. Los tests de límite documentan ese umbral.

    // Caso base: porcentajes que suman exactamente 100%. Debe distribuir sin lanzar
    // ninguna excepción.
    @Test
    void distribuir_PorcentajesSuman100Exacto_DebeDistribuirSinExcepcion() {
        PresupuestoMensual presupuesto = new PresupuestoMensual(new Ingreso(1_000_000.0, new Date()));
        Map<Categoria, Double> porcentajes = Map.of(
                Categoria.SERVICIOS, 60.0,
                Categoria.ALIMENTACION, 40.0);

        assertDoesNotThrow(() -> presupuesto.distribuir(porcentajes));
    }

    // Suma = 99.95: diferencia de 0.05 con respecto a 100, dentro del margen +-
    // 0.1. Deberia ser aceptado sin excepción.
    @Test
    void distribuir_PorcentajesSuman99_95_DentroDelMargen_DebeAceptar() {
        PresupuestoMensual presupuesto = new PresupuestoMensual(new Ingreso(1_000_000.0, new Date()));
        Map<Categoria, Double> porcentajes = new HashMap<>();
        porcentajes.put(Categoria.SERVICIOS, 60.0);
        porcentajes.put(Categoria.ALIMENTACION, 39.95);// suma = 99.95

        assertDoesNotThrow(() -> presupuesto.distribuir(porcentajes));
    }

    // Suma = 100.05: diferencia de 0.05 por encima de 100, dentro del margen +-
    // 0.1. Deberia ser aceptado sin excepción.
    @Test
    void distribuir_PorcentajesSuman100_05_DentroDelMargen_DebeAceptar() {
        PresupuestoMensual presupuesto = new PresupuestoMensual(new Ingreso(1_000_000.0, new Date()));
        Map<Categoria, Double> porcentajes = new HashMap<>();
        porcentajes.put(Categoria.SERVICIOS, 60.0);
        porcentajes.put(Categoria.ALIMENTACION, 40.05); // suma = 100.05

        assertDoesNotThrow(() -> presupuesto.distribuir(porcentajes));
    }

    // Suma = 99.89: diferencia de 0.11 por debajo de 100, fuera del margen +- 0.1.
    // Deberia lanzar IllegalArgumentException. Este test busca documenta el límite
    // exacto del umbral codificado en validarPorcentajes().
    @Test
    void distribuir_PorcentajesSuman99_89_FueraDelMargen_DebeLanzarExcepcion() {
        PresupuestoMensual presupuesto = new PresupuestoMensual(new Ingreso(1_000_000.0, new Date()));
        Map<Categoria, Double> porcentajes = new HashMap<>();
        porcentajes.put(Categoria.SERVICIOS, 60.0);
        porcentajes.put(Categoria.ALIMENTACION, 39.89);// suma = 99.89

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> presupuesto.distribuir(porcentajes));

        assertTrue(ex.getMessage().startsWith("La suma debe ser 100%"));
    }

    // Suma = 100.11: diferencia de 0.11 por encima de 100, fuera del margen +- 0.1.
    // Deberia lanzar IllegalArgumentException.
    @Test
    void distribuir_PorcentajesSuman100_11_FueraDelMargen_DebeLanzarExcepcion() {
        PresupuestoMensual presupuesto = new PresupuestoMensual(new Ingreso(1_000_000.0, new Date()));
        Map<Categoria, Double> porcentajes = new HashMap<>();
        porcentajes.put(Categoria.SERVICIOS, 60.0);
        porcentajes.put(Categoria.ALIMENTACION, 40.11);// suma = 100.11

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> presupuesto.distribuir(porcentajes));
        assertTrue(ex.getMessage().startsWith("La suma debe ser 100%"));
    }

    // Mapa vacío: suma = 0.0, que está completamente fuera del rango aceptado.
    // Deberia lanzar IllegalArgumentException — caso borde extremo.
    @Test
    void distribuir_MapaVacio_SumaCero_DebeLanzarExcepcion() {
        PresupuestoMensual presupuesto = new PresupuestoMensual(new Ingreso(1_000_000.0, new Date()));
        Map<Categoria, Double> porcentajes = new HashMap<>();// vacío, suma = 0.0

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> presupuesto.distribuir(porcentajes));
        assertTrue(ex.getMessage().startsWith("La suma debe ser 100%"));
    }

    // Dado un monto total de 1000000 con 30% para SERVICIOS y 70% para
    // ALIMENTACION, los montos asignados deben calcularse correctamente. Se usa
    // delta 0.001 para tolerancia de punto flotante.
    @Test
    void distribuir_MontoYPorcentajes_DebeCalcularMontosCorrectamente() {
        PresupuestoMensual presupuesto = new PresupuestoMensual(new Ingreso(1_000_000.0, new Date()));
        Map<Categoria, Double> porcentajes = Map.of(
                Categoria.SERVICIOS, 30.0,
                Categoria.ALIMENTACION, 70.0);

        presupuesto.distribuir(porcentajes);

        Map<Categoria, Double> distribucion = presupuesto.obtenerDistribucion();
        assertEquals(300_000.0, distribucion.get(Categoria.SERVICIOS), 0.001);
        assertEquals(700_000.0, distribucion.get(Categoria.ALIMENTACION), 0.001);
    }

    // Si distribuir() se llama dos veces con mapas distintos, la segunda llamada
    // debe sobreescribir completamente la primera (por el clear() interno).
    // Las categorías de la primera distribución no deben aparecer en el resultado.
    @Test
    void distribuir_SegundaLlamada_DebeSObreescribirDistribucionAnterior() {
        PresupuestoMensual presupuesto = new PresupuestoMensual(new Ingreso(1_000_000.0, new Date()));
        Map<Categoria, Double> primera = Map.of(
                Categoria.SERVICIOS, 50.0,
                Categoria.TRANSPORTE, 50.0);
        Map<Categoria, Double> segunda = Map.of(
                Categoria.ALIMENTACION, 60.0,
                Categoria.SALUD, 40.0);

        presupuesto.distribuir(primera);
        presupuesto.distribuir(segunda);

        Map<Categoria, Double> distribucionFinal = presupuesto.obtenerDistribucion();
        assertFalse(distribucionFinal.containsKey(Categoria.SERVICIOS));
        assertFalse(distribucionFinal.containsKey(Categoria.TRANSPORTE));
        assertEquals(600_000.0, distribucionFinal.get(Categoria.ALIMENTACION), 0.001);
        assertEquals(400_000.0, distribucionFinal.get(Categoria.SALUD), 0.001);
    }

    // obtenerDistribucion() retorna new HashMap<>(montosPorCategoria), una copia
    // independiente del mapa interno. Mutar el mapa retornado no debe modificar el
    // estado del presupuesto.
    @Test
    void obtenerDistribucion_ModificarResultado_NoDebeAfectarEstadoInterno() {
        PresupuestoMensual presupuesto = new PresupuestoMensual(new Ingreso(1_000_000.0, new Date()));
        presupuesto.distribuir(Map.of(Categoria.SERVICIOS, 100.0));

        Map<Categoria, Double> copia = presupuesto.obtenerDistribucion();
        copia.put(Categoria.TRANSPORTE, 999_999.0);

        Map<Categoria, Double> distribucionInterna = presupuesto.obtenerDistribucion();
        assertFalse(distribucionInterna.containsKey(Categoria.TRANSPORTE));
        assertEquals(1, distribucionInterna.size());
    }

    // Antes de llamar a distribuir(), el mapa de montos está vacío.
    // obtenerDistribucion() debe retornar un mapa vacío (no null).
    @Test
    void obtenerDistribucion_AntesDeDistribuir_DebeRetornarMapaVacio() {
        Ingreso ingreso = new Ingreso(1_000_000.0, new Date());

        PresupuestoMensual presupuesto = new PresupuestoMensual(ingreso);
        Map<Categoria, Double> distribucion = presupuesto.obtenerDistribucion();

        assertNotNull(distribucion);
        assertTrue(distribucion.isEmpty());
    }
}
