package com.example.fintrack_webapi.application.dto.commands;

import jakarta.validation.constraints.*;
import java.util.Map;

public record IngresoDTO(

    @Positive(message = "El monto debe ser mayor a 0")
    double monto,

    @NotBlank(message = "La fecha es obligatoria")
    String fecha,

    @NotNull(message = "Los porcentajes son obligatorios")
    Map<Integer, Double> porcentajes

) {}