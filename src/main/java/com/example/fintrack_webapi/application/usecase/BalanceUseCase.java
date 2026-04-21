package com.example.fintrack_webapi.application.usecase;

import com.example.fintrack_webapi.application.dto.queries.BalanceDTO;
import com.example.fintrack_webapi.domain.model.Egreso;
import com.example.fintrack_webapi.domain.model.Transaccion;
import com.example.fintrack_webapi.domain.port.input.BalanceUseCasePort;
import com.example.fintrack_webapi.domain.port.output.TransaccionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BalanceUseCase implements BalanceUseCasePort {

    private final TransaccionRepositoryPort transaccionRepo;

    public BalanceUseCase(TransaccionRepositoryPort transaccionRepo) {
        this.transaccionRepo = transaccionRepo;
    }

    @Override
    public BalanceDTO obtenerBalance() {

        List<Transaccion> transacciones = transaccionRepo.obtenerHistorial();

        double ingresos = calcularIngresos(transacciones);
        double gastos = calcularGastos(transacciones);

        return new BalanceDTO(ingresos, gastos, ingresos - gastos);
    }

    private double calcularIngresos(List<Transaccion> transacciones) {
        return transacciones.stream()
                .filter(t -> !(t instanceof Egreso))
                .mapToDouble(Transaccion::getMonto)
                .sum();
    }

    private double calcularGastos(List<Transaccion> transacciones) {
        return transacciones.stream()
                .filter(t -> t instanceof Egreso)
                .mapToDouble(Transaccion::getMonto)
                .sum();
    }
}