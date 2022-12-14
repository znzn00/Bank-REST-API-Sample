package com.zhen.backend.exception;

public class MovimientoCreateHaveSaldoException extends RuntimeException {
    public MovimientoCreateHaveSaldoException() {
        super("Los movimientos a crear no deberían de contener saldo. Esto es calculado.");
    }
}
