package com.zhen.backend.dto;

import com.zhen.backend.model.enumerables.EstadoMovimiento;
import com.zhen.backend.model.enumerables.TipoMovimiento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class MovimientoDtoPostIn {
    @NotNull
    private Long cuenta;
    @NotNull
    private TipoMovimiento tipo;
    @NotNull
    private EstadoMovimiento estado;
    @NotNull
    private BigDecimal valor;
    @NotNull
    private String descripcion;

    public MovimientoDtoPostIn(MovimientoDtoPutIn movimientoDtoPutIn) {
        this.cuenta = movimientoDtoPutIn.getCuenta();
        this.tipo = movimientoDtoPutIn.getTipo();
        this.estado = movimientoDtoPutIn.getEstado();
        this.valor = movimientoDtoPutIn.getValor();
        this.descripcion = movimientoDtoPutIn.getDescripcion();
    }
}
