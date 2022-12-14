package com.zhen.backend.dto;

import com.zhen.backend.model.Cuenta;
import com.zhen.backend.model.enumerables.EstadoCuenta;
import com.zhen.backend.model.enumerables.TipoCuenta;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class CuentaDtoEstado {
    private Long numero;
    private TipoCuenta tipo;
    private BigDecimal saldo;
    private EstadoCuenta estado;
    private BigDecimal totalDebito;
    private BigDecimal totalCredito;
    public CuentaDtoEstado(Cuenta cuenta, BigDecimal saldo) {
        this.numero = cuenta.getNumeroCuenta();
        this.tipo = cuenta.getTipo();
        this.saldo = saldo;
        this.estado = cuenta.getEstado();
    }

}
