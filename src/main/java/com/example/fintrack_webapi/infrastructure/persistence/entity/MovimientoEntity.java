package com.example.fintrack_webapi.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Entity
@Table(name = "movimiento")
@IdClass(MovimientoEntity.MovimientoId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoEntity {

    @Id
    @Column(name = "tipotransferencia", insertable = false, updatable = false)
    private String tipoTransferencia;

    @Id
    @Column(name = "idtransferencia", insertable = false, updatable = false)
    private Integer idTransferencia;

    public static class MovimientoId implements Serializable {
        private String tipoTransferencia;
        private Integer idTransferencia;

        public MovimientoId() {}

        public MovimientoId(String tipoTransferencia, Integer idTransferencia) {
            this.tipoTransferencia = tipoTransferencia;
            this.idTransferencia = idTransferencia;
        }

        public String getTipoTransferencia() { return tipoTransferencia; }
        public void setTipoTransferencia(String tipoTransferencia) { this.tipoTransferencia = tipoTransferencia; }
        public Integer getIdTransferencia() { return idTransferencia; }
        public void setIdTransferencia(Integer idTransferencia) { this.idTransferencia = idTransferencia; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MovimientoId that = (MovimientoId) o;
            return Objects.equals(tipoTransferencia, that.tipoTransferencia) &&
                   Objects.equals(idTransferencia, that.idTransferencia);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tipoTransferencia, idTransferencia);
        }
    }
}