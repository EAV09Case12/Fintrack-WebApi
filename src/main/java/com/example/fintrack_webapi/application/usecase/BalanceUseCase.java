package com.example.fintrack_webapi.application.usecase;

import com.example.fintrack_webapi.application.dto.queries.BalanceDTO;
import com.example.fintrack_webapi.domain.port.input.BalanceUseCasePort;
import com.example.fintrack_webapi.domain.port.output.TransaccionRepositoryPort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Date;
import com.example.fintrack_webapi.domain.exception.BadRequestException;

@Service
public class BalanceUseCase implements BalanceUseCasePort {

    private final TransaccionRepositoryPort transaccionRepo;

    public BalanceUseCase(TransaccionRepositoryPort transaccionRepo) {
        this.transaccionRepo = transaccionRepo;
    }

    @Override
    public BalanceDTO obtenerBalance(String fechaStr) {

        Date fecha = obtenerFechaReferencia(fechaStr);

        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);

        int mes = cal.get(Calendar.MONTH) + 1; 
        int anio = cal.get(Calendar.YEAR);

        double[] resultado = transaccionRepo.obtenerBalanceMensual(mes, anio);

        double ingresos = resultado[0];
        double gastos = resultado[1];

        double balance = ingresos - gastos;

        double total = ingresos + gastos;

        double porcentajeGastos = ingresos == 0 ? 0 : (gastos / ingresos) * 100;
        double porcentajeAhorro = ingresos == 0 ? 0 : (balance / ingresos) * 100;

        return new BalanceDTO(
        ingresos,
        gastos,
        balance,
        porcentajeGastos,
        porcentajeAhorro
    );
    }

    private Date obtenerFechaReferencia(String fechaStr) {
        try {
            if (fechaStr == null || fechaStr.isBlank()) {
                return new Date(); // mes actual
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);

            return sdf.parse(fechaStr);

        } catch (Exception e) {
            throw new BadRequestException("Formato de fecha inválido. Usa yyyy-MM-dd");
        }
    }

}