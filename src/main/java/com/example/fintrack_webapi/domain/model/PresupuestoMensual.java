package com.example.fintrack_webapi.domain.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class PresupuestoMensual {

    private final Date fecha;
    private final double montoTotal;
    private final Map<Categoria, Double> montosPorCategoria = new HashMap<>();

    public PresupuestoMensual(Ingreso ingreso) {
        this.fecha = ingreso.getFecha();
        this.montoTotal = ingreso.getMonto();
    }

    public void distribuir(Map<Categoria, Double> porcentajes) {

        validarPorcentajes(porcentajes);

        montosPorCategoria.clear(); 

        for (Map.Entry<Categoria, Double> entry : porcentajes.entrySet()) {

            Categoria categoria = entry.getKey();
            double porcentaje = entry.getValue();

            double montoAsignado = montoTotal * (porcentaje / 100.0);

            montosPorCategoria.put(categoria, montoAsignado);
        }
    }

    private void validarPorcentajes(Map<Categoria, Double> porcentajes) {

        double suma = porcentajes.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        if (Math.abs(suma - 100.0) > 0.0001) {
            throw new IllegalArgumentException("La suma debe ser 100%");
        }
    }

    public Map<Categoria, Double> obtenerDistribucion() {
        return new HashMap<>(montosPorCategoria);
    }
}