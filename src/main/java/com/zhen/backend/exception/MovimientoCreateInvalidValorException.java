package com.zhen.backend.exception;

public class MovimientoCreateInvalidValorException extends RuntimeException{
    public MovimientoCreateInvalidValorException() {
        super("El valor es inválido para este tipo de movimiento.");
    }
}
