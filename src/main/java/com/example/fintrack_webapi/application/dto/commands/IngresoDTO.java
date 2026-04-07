package com.example.fintrack_webapi.application.dto.commands;

import jakarta.validation.constraints.*;
import java.util.Map;

public record IngresoDTO(

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "200.0", inclusive = true, message = "El monto de ingreso debe ser mayor o igual a 200")
    Double monto,

    @NotBlank(message = "La fecha es obligatoria")
    String fecha,

    @NotNull(message = "Los porcentajes son obligatorios")
    Map<Integer, Double> porcentajes

) {}