package com.example.fintrack_webapi.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "movimiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoEntity {

    @Id
    @Column(name = "idMov")
    private Long id;

    @Column
    private double monto;

    @Column
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @Column(nullable = false)
    private int categoria; // FK (guardas el código del enum)

    @Column
    private String descripcion;
}