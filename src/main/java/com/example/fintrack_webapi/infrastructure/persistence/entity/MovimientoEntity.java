package com.example.fintrack_webapi.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "movimiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoEntity {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_transferencia")
    private String tipoTransferencia;

    @Column(name = "id_transferencia")
    private Long idTransferencia;

    @Column(name = "email_usuario")
    private String emailUsuario;

    @Column(name = "fecha_movimiento")
    private LocalDateTime fechaMovimiento;
}