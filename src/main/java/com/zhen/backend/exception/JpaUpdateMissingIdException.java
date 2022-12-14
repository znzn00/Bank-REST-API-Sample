package com.zhen.backend.exception;

public class JpaUpdateMissingIdException extends RuntimeException {
    public JpaUpdateMissingIdException() {
        super("El objecto a actualizar no tiene ID.");
    }
}
