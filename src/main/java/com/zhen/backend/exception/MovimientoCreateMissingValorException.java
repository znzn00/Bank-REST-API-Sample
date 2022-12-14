package com.zhen.backend.exception;

public class MovimientoCreateMissingValorException extends RuntimeException {
    public MovimientoCreateMissingValorException() {
        super("El movimiento necesita tener un valor definido.");
    }
}
