package com.example.fintrack_webapi.domain.port.input;

import com.example.fintrack_webapi.application.dto.queries.MovimientoDTO;
import java.util.List;

public interface ConsultaUseCasePort {
	List<MovimientoDTO> obtenerHistorial();

	List<MovimientoDTO> obtenerUltimosMovimientos(int cantidad);

	List<MovimientoDTO> obtenerPorCategoria(int codigoCategoria);
}
