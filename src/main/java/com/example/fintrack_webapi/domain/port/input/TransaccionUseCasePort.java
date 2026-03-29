package com.example.fintrack_webapi.domain.port.input;

import com.example.fintrack_webapi.application.dto.IngresoDTO;
import com.example.fintrack_webapi.application.dto.PresupuestoResponseDTO;

public interface TransaccionUseCasePort {
    PresupuestoResponseDTO registrarIngreso(IngresoDTO dto);
}
