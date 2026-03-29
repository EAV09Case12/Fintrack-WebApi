package com.example.fintrack_webapi.domain.port.output;

import com.example.fintrack_webapi.domain.model.Ingreso;

public interface TransaccionRepositoryPort {
    void guardarIngreso(Ingreso ingreso);
}
