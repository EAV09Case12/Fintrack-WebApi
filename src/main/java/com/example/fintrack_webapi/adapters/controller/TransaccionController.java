package com.example.fintrack_webapi.adapters.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.example.fintrack_webapi.application.dto.commands.EgresoDTO;
import com.example.fintrack_webapi.application.dto.commands.IngresoDTO;
import com.example.fintrack_webapi.application.dto.queries.PresupuestoResponseDTO;
import com.example.fintrack_webapi.application.usecase.TransaccionUseCase;
import org.springframework.security.access.prepost.PreAuthorize;



@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
@Tag(name= "Creación de entidades", description = "Endpoints de creación y gestión de entidades")
public class TransaccionController {

    private final TransaccionUseCase transaccionUseCase;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/SaveIngreso")
    @Operation(summary = "Registrar ingreso", description = "Crear un ingreso y su respectivo presupuesto mensual")
    public ResponseEntity<PresupuestoResponseDTO> registrarIngreso(@Valid @RequestBody IngresoDTO dto){
        PresupuestoResponseDTO response = transaccionUseCase.registrarIngreso(dto);
        return ResponseEntity.ok(response);
    }
    
    
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/egreso")
    @Operation(summary = "Registrar egreso", description = "Crear un egreso")
    public ResponseEntity<Void> registrarEgreso(@Valid @RequestBody EgresoDTO dto) {
        transaccionUseCase.registrarEgreso(dto);
        return ResponseEntity.ok().build();
    }

}
