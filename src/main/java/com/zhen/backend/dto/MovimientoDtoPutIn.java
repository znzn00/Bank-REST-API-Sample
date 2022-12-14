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
public class MovimientoDtoPutIn {
    private Long id;
    private Long cuenta;
    private TipoMovimiento tipo;
    private EstadoMovimiento estado;
    private BigDecimal valor;
    private String descripcion;
}
