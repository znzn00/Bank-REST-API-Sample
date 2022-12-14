package com.zhen.backend.dto;

import com.zhen.backend.model.Movimiento;
import com.zhen.backend.model.enumerables.EstadoMovimiento;
import com.zhen.backend.model.enumerables.TipoMovimiento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class MovimientoDtoOut {
    private Long id;
    private Long cuenta;
    private TipoMovimiento tipo;
    private EstadoMovimiento estado;
    private BigDecimal valor;
    private TipoMovimiento.Orientacion movimiento;
    private String descripcion;
    private LocalDateTime fecha;

    public MovimientoDtoOut(Movimiento movimiento) {
        this.id = movimiento.getId();
        this.cuenta = movimiento.getCuentaId();
        this.tipo = movimiento.getTipo();
        this.estado = movimiento.getEstado();
        BigDecimal temporal = movimiento.getValor();
        if(temporal.signum()==1) {
            this.valor = temporal;
            this.movimiento = TipoMovimiento.Orientacion.CREDITO;
        } else {
            this.valor = temporal.negate();
            this.movimiento = TipoMovimiento.Orientacion.DEBITO;
        }
        this.descripcion = movimiento.getDescripcion();
        this.fecha = movimiento.getCreationTimestamp();

    }
}
