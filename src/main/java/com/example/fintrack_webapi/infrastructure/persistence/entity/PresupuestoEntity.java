package com.example.fintrack_webapi.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;
@Entity
@Table(name = "presupuesto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresupuestoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPre")
    private Long id;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @Column(nullable = false)
    private double montoTotal;

    @Column
    private Double serviciosCat;
    @Column
    private Double entretenimientoCat;
    @Column
    private Double transporteCat;
    @Column
    private Double alimentacionCat;
    @Column
    private Double saludCat;
    @Column
    private Double deudasCat;
}