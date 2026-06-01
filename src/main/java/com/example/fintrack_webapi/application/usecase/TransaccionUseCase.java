package com.example.fintrack_webapi.application.usecase;

import com.example.fintrack_webapi.application.dto.commands.IngresoDTO;
import com.example.fintrack_webapi.application.dto.commands.EgresoDTO;
import com.example.fintrack_webapi.application.dto.queries.PresupuestoResponseDTO;
import com.example.fintrack_webapi.domain.model.*;
import com.example.fintrack_webapi.domain.port.input.TransaccionUseCasePort;
import com.example.fintrack_webapi.domain.port.output.TransaccionRepositoryPort;
import com.example.fintrack_webapi.domain.port.output.PresupuestoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.fintrack_webapi.domain.exception.BadRequestException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransaccionUseCase implements TransaccionUseCasePort {

    private final TransaccionRepositoryPort transaccionRepo;
    private final PresupuestoRepositoryPort presupuestoRepo;

    public TransaccionUseCase(
            TransaccionRepositoryPort transaccionRepo,
            PresupuestoRepositoryPort presupuestoRepo) {
        this.transaccionRepo = transaccionRepo;
        this.presupuestoRepo = presupuestoRepo;
    }


    @Override
    @Transactional
    public PresupuestoResponseDTO registrarIngreso(IngresoDTO dto) {

        if (dto.monto() <= 0) {
        throw new BadRequestException("El monto debe ser mayor a 0");
        }

        Date fecha = convertirFecha(dto.fecha());

        Ingreso ingreso = new Ingreso(dto.monto(), fecha);

        PresupuestoMensual presupuesto = new PresupuestoMensual(ingreso);
        Map<Categoria, Double> porcentajesPorCategoria = convertirCategorias(dto.porcentajes());

        try {
            presupuesto.distribuir(porcentajesPorCategoria);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("La suma de los porcentajes debe dar cien");
        }

        ingreso = transaccionRepo.guardarIngreso(ingreso);

        presupuesto = presupuestoRepo.guardar(presupuesto);

        return toResponse(presupuesto);
    }


    @Override
    public void registrarEgreso(EgresoDTO dto) {

        if (dto.monto() <= 0) {
            throw new BadRequestException("El monto debe ser mayor a 0");
        }

        Date fecha = convertirFecha(dto.fecha());

        Egreso egreso = new Egreso(
                dto.monto(),
                fecha,
                buscarPorCodigo(dto.codigoCategoria()),
                dto.descripcion()
        );

        transaccionRepo.guardarEgreso(egreso);

    }


    private Date convertirFecha(String fechaStr) {
    try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false); // formato estricto

        Date fecha = sdf.parse(fechaStr);

       
        Date hoy = new Date();

        long unAnio = 1000L * 60 * 60 * 24 * 365;

        Date limitePasado = new Date(hoy.getTime() - unAnio);

        if (fecha.before(limitePasado) || fecha.after(hoy)) {
            throw new BadRequestException(
                "La fecha debe estar comprendida desde un año atrás hasta la fecha actual."
            );
        }

        return fecha;

    } catch (BadRequestException e) {
        throw e;
    } catch (Exception e) {
        throw new BadRequestException("Formato de fecha inválido. Usa yyyy-MM-dd");
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

        throw new BadRequestException("Categoría inválida");
    }

    private PresupuestoResponseDTO toResponse(PresupuestoMensual p) {

        Map<String, Double> resultado = new HashMap<>();

        for (var entry : p.obtenerDistribucion().entrySet()) {
            resultado.put(entry.getKey().name(), entry.getValue());
        }

        return new PresupuestoResponseDTO(
            p.getFecha().toString(),
            p.getMontoTotal(),
            resultado
        );
    }
}