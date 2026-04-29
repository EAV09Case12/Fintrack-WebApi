package com.example.fintrack_webapi.application.usecase;

import com.example.fintrack_webapi.application.dto.queries.BalanceDTO;
import com.example.fintrack_webapi.domain.port.input.BalanceUseCasePort;
import com.example.fintrack_webapi.domain.port.output.BalanceRepositoryPort;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Date;
import com.example.fintrack_webapi.domain.exception.BadRequestException;

@Service
public class BalanceUseCase implements BalanceUseCasePort {

    private final BalanceRepositoryPort balanceRepo;

    public BalanceUseCase(BalanceRepositoryPort balanceRepo) {
        this.balanceRepo = balanceRepo;
    }

    @Override
    public BalanceDTO obtenerBalance(String fechaStr) {

        Date fecha = obtenerFechaReferencia(fechaStr);

        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);

        int mes = cal.get(Calendar.MONTH) + 1; 
        int anio = cal.get(Calendar.YEAR);

       double ingresos = balanceRepo.obtenerTotalIngresos(mes, anio);
       double gastos = balanceRepo.obtenerTotalGastos(mes, anio);

        double balance = ingresos - gastos;

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