package com.example.fintrack_webapi.infrastructure.persistence.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "aud_movimiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipotransferencia", nullable = false)
    private String tipoTransferencia;

    @Column(name = "idtransferencia", nullable = false)
    private Long idTransferencia;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "realizado_en")
    private LocalDateTime realizadoEn;
}