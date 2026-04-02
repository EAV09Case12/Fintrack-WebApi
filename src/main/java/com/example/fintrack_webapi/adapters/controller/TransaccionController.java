package com.example.fintrack_webapi.adapters.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fintrack_webapi.application.dto.commands.EgresoDTO;
import com.example.fintrack_webapi.application.dto.commands.IngresoDTO;
import com.example.fintrack_webapi.application.dto.queries.PresupuestoResponseDTO;
import com.example.fintrack_webapi.application.usecase.TransaccionUseCase;
import com.example.fintrack_webapi.domain.model.Ingreso;
import com.example.fintrack_webapi.domain.model.Egreso;


@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
@Tag(name= "Creación de entidades", description = "Endpoints de creación y gestión de entidades")
public class TransaccionController {

    private final TransaccionUseCase transaccionUseCase;

    @PostMapping("/SaveIngreso")
    @Operation(summary = "Registrar ingreso", description = "Crear un ingreso y su respectivo presupuesto mensual")
    public PresupuestoResponseDTO registrarIngreso(@RequestBody IngresoDTO dto) {
        //TODO: process POST request
        
        return transaccionUseCase.registrarIngreso(dto);
    }
    
    
    @PostMapping("/egreso")
    @Operation(summary = "Registrar egreso", description = "Crear un egreso")
    public void registrarEgreso(@RequestBody EgresoDTO dto) {

        transaccionUseCase.registrarEgreso(dto);
    }

}
