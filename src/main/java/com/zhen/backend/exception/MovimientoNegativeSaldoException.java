package com.zhen.backend.exception;

public class MovimientoNegativeSaldoException extends RuntimeException{
    public MovimientoNegativeSaldoException() {
        super("El saldo no puede ser negativc.");
    }
}
