package com.example.fintrack_webapi.adapters.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.fintrack_webapi.application.dto.commands.EgresoDTO;
import com.example.fintrack_webapi.application.dto.commands.IngresoDTO;
import com.example.fintrack_webapi.application.usecase.TransaccionUseCase;
import org.springframework.security.access.prepost.PreAuthorize;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
@Tag(name= "Creación de entidades", description = "Endpoints de creación y gestión de entidades")
public class TransaccionController {

    private final TransaccionUseCase transaccionUseCase;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/SaveIngreso")
    @Operation(
        summary = "Registrar ingreso",
        responses = {
            @ApiResponse(responseCode = "201", description = "Ingreso creado correctamente", content = @Content),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
        }
    )
    public ResponseEntity<Void> registrarIngreso(@Valid @RequestBody IngresoDTO dto){
        transaccionUseCase.registrarIngreso(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/egreso")
    @Operation(
        summary = "Registrar egreso",
        responses = {
            @ApiResponse(responseCode = "201", description = "Egreso creado correctamente", content = @Content),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
        }
    )
    public ResponseEntity<Void> registrarEgreso(@Valid @RequestBody EgresoDTO dto) {
        transaccionUseCase.registrarEgreso(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}