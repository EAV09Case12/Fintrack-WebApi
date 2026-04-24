package com.example.fintrack_webapi.domain.port.input;

import com.example.fintrack_webapi.application.dto.queries.BalanceDTO;

public interface BalanceUseCasePort {
    BalanceDTO obtenerBalance(String fecha);
}