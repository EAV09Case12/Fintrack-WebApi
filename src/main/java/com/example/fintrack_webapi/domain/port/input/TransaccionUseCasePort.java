package com.example.fintrack_webapi.domain.port.input;

import com.example.fintrack_webapi.application.dto.commands.EgresoDTO;
import com.example.fintrack_webapi.application.dto.commands.IngresoDTO;
import com.example.fintrack_webapi.application.dto.queries.PresupuestoResponseDTO;

public interface TransaccionUseCasePort {
    PresupuestoResponseDTO registrarIngreso(IngresoDTO dto);
    void registrarEgreso(EgresoDTO dto);
}
