package com.zhen.backend.dto;

import com.zhen.backend.model.Cuenta;
import com.zhen.backend.model.enumerables.EstadoCuenta;
import com.zhen.backend.model.enumerables.TipoCuenta;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class CuentaDtoOut {
    private Long numero;
    private Long cliente;
    private TipoCuenta tipo;
    private BigDecimal saldoDisponible;
    private EstadoCuenta estado;

    public CuentaDtoOut(Cuenta cuenta) {
        this.numero = cuenta.getNumeroCuenta();
        this.cliente = cuenta.getClienteId();
        this.tipo = cuenta.getTipo();
        this.saldoDisponible = cuenta.getSaldoInicial();
        this.estado = cuenta.getEstado();
    }
    public CuentaDtoOut(Cuenta cuenta, BigDecimal saldo) {
        this.numero = cuenta.getNumeroCuenta();
        this.cliente = cuenta.getClienteId();
        this.tipo = cuenta.getTipo();
        this.saldoDisponible = saldo;
        this.estado = cuenta.getEstado();
    }
}
