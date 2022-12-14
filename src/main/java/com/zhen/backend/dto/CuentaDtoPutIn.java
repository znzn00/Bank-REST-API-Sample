package com.zhen.backend.dto;

import com.zhen.backend.model.enumerables.EstadoCuenta;
import com.zhen.backend.model.enumerables.TipoCuenta;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class CuentaDtoPutIn{
    private Long numeroCuenta;
    private EstadoCuenta estado;
    private Long cliente;
    private TipoCuenta tipo;
    private BigDecimal saldoInicial;
}
