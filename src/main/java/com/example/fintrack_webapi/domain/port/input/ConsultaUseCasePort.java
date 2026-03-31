package com.example.fintrack_webapi.domain.port.input;

import com.example.fintrack_webapi.application.dto.queries.MovimientoDTO;
import com.example.fintrack_webapi.application.dto.queries.EgresoResponseDTO;
import java.util.List;

public interface ConsultaUseCasePort {

	List<MovimientoDTO> obtenerUltimosMovimientos(int cantidad);

	List<EgresoResponseDTO> obtenerPorCategoria(int codigoCategoria);

}
