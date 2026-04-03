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
    @Column(name = "idpre")
    private Long id;

    @Column(name = "fecha", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @Column(name = "montototal", nullable = false)  // ← sin guión bajo, todo junto
    private double montoTotal;

    @Column(name = "servicioscat")
    private Double serviciosCat;
    
    @Column(name = "entretenimientocat")
    private Double entretenimientoCat;
    
    @Column(name = "transportecat")
    private Double transporteCat;
    
    @Column(name = "alimentacioncat")
    private Double alimentacionCat;
    
    @Column(name = "saludcat")
    private Double saludCat;
    
    @Column(name = "deudascat")
    private Double deudasCat;
}