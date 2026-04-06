package com.example.fintrack_webapi.application.dto.commands;

import jakarta.validation.constraints.*;

public record EgresoDTO(

    @Positive(message = "El monto debe ser mayor a 0")
    double monto,

    @NotBlank(message = "La fecha es obligatoria")
    String fecha,

    @Positive(message = "El código de categoría debe ser válido")
    int codigoCategoria,

    @NotBlank(message = "La descripción es obligatoria")
    String descripcion

) {}