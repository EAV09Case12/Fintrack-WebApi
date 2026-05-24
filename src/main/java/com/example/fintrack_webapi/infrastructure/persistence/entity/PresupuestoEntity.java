package com.example.fintrack_webapi.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.IdClass;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;
import java.io.Serializable;
import java.util.Objects;
import java.math.BigDecimal;

@Entity
@Table(name = "presupuesto")
@IdClass(PresupuestoEntity.PresupuestoId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresupuestoEntity {

    @Id
    @Column(name = "fecha", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @Id
    @Column(name = "idcat", nullable = false)
    private Integer idCat;

    @Id
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PresupuestoId implements Serializable {

        private Date fecha;

        private Integer idCat;

        private String userEmail;

        @Override
        public boolean equals(Object o) {

            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            PresupuestoId that = (PresupuestoId) o;

            return Objects.equals(fecha, that.fecha)
                    && Objects.equals(idCat, that.idCat)
                    && Objects.equals(userEmail, that.userEmail);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fecha, idCat, userEmail);
        }
    }
}