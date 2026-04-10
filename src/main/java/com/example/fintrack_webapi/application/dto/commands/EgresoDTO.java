package com.example.fintrack_webapi.application.dto.commands;

import jakarta.validation.constraints.*;

public record EgresoDTO(

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "1.0", inclusive = true, message = "El monto de egreso debe ser mayor o igual a 1")
    Double monto,

    @NotBlank(message = "La fecha es obligatoria")
    String fecha,

    @Positive(message = "El código de categoría debe ser válido")
    int codigoCategoria,

    @NotBlank(message = "La descripción es obligatoria")
    String descripcion

) {}