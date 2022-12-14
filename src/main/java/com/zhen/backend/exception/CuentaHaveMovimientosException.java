package com.zhen.backend.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class CuentaHaveMovimientosException extends RuntimeException {
    private List<Long> movimientos;

    public CuentaHaveMovimientosException(Long numeroCuenta, List<Long> movimientos) {
        super("La cuenta con numero \"" + numeroCuenta + "\" tiene cuentas.");
        this.movimientos = movimientos;
    }
}
