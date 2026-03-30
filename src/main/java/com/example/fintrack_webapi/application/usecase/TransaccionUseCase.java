package com.example.fintrack_webapi.application.usecase;

import com.example.fintrack_webapi.application.dto.commands.IngresoDTO;
import com.example.fintrack_webapi.application.dto.commands.EgresoDTO;
import com.example.fintrack_webapi.application.dto.queries.PresupuestoResponseDTO;
import com.example.fintrack_webapi.domain.model.*;
import com.example.fintrack_webapi.domain.port.input.TransaccionUseCasePort;
import com.example.fintrack_webapi.domain.port.output.TransaccionRepositoryPort;
import com.example.fintrack_webapi.domain.port.output.PresupuestoRepositoryPort;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TransaccionUseCase implements TransaccionUseCasePort {

    private final TransaccionRepositoryPort transaccionRepo;
    private final PresupuestoRepositoryPort presupuestoRepo;

    public TransaccionUseCase(
            TransaccionRepositoryPort transaccionRepo,
            PresupuestoRepositoryPort presupuestoRepo) {
        this.transaccionRepo = transaccionRepo;
        this.presupuestoRepo = presupuestoRepo;
    }

    // =========================
    // INGRESO
    // =========================
    @Override
    public PresupuestoResponseDTO registrarIngreso(IngresoDTO dto) {

        // 1. convertir fecha
        Date fecha = convertirFecha(dto.fecha());

        // 2. crear entidad de dominio (Liskov aplicado)
        Ingreso ingreso = new Ingreso(dto.monto(), fecha);

        // 3. guardar ingreso (tabla ingreso)
        ingreso = transaccionRepo.guardarIngreso(ingreso);

        // ⚠ movimiento lo crea el trigger en DB

        // 4. convertir porcentajes (DTO → dominio)
        Map<Categoria, Double> porcentajes = convertirCategorias(dto.porcentajes());

        // 5. crear presupuesto desde ingreso
        PresupuestoMensual presupuesto = new PresupuestoMensual(ingreso);

        // 6. aplicar lógica de dominio
        presupuesto.distribuir(porcentajes);

        // 7. guardar presupuesto (tabla presupuesto)
        presupuesto = presupuestoRepo.guardar(presupuesto);

        // 8. devolver DTO de respuesta
        return toResponse(presupuesto);
    }

    // =========================
    // EGRESO
    // =========================
    @Override
    public void registrarEgreso(EgresoDTO dto) {

        Date fecha = convertirFecha(dto.fecha());

        Egreso egreso = new Egreso(
                dto.monto(),
                fecha,
                buscarPorCodigo(dto.codigoCategoria()),
                dto.descripcion()
        );

        // guardar en tabla egreso
        transaccionRepo.guardarEgreso(egreso);

        // ⚠ movimiento lo crea el trigger
    }

    // =========================
    // MÉTODOS PRIVADOS
    // =========================

    private Date convertirFecha(String fechaStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(fechaStr);
        } catch (Exception e) {
            throw new RuntimeException("Formato de fecha inválido");
        }
    }

    private Map<Categoria, Double> convertirCategorias(Map<Integer, Double> entrada) {

        Map<Categoria, Double> resultado = new HashMap<>();

        for (Map.Entry<Integer, Double> entry : entrada.entrySet()) {

            Categoria categoria = buscarPorCodigo(entry.getKey());

            resultado.put(categoria, entry.getValue());
        }

        return resultado;
    }

    private Categoria buscarPorCodigo(int codigo) {

        for (Categoria c : Categoria.values()) {
            if (c.getCodigo() == codigo) {
                return c;
            }
        }

        throw new RuntimeException("Categoría inválida");
    }

    private PresupuestoResponseDTO toResponse(PresupuestoMensual p) {

        Map<String, Double> resultado = new HashMap<>();

        for (var entry : p.obtenerDistribucion().entrySet()) {
            resultado.put(entry.getKey().name(), entry.getValue());
        }

        return new PresupuestoResponseDTO(
                null, // ⚠ lo asigna JPA después (ajustar cuando tengas entity)
                p.getFecha().toString(),
                p.getMontoTotal(),
                resultado
        );
    }
}