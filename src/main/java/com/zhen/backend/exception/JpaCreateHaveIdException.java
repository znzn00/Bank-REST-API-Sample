package com.zhen.backend.exception;

public class JpaCreateHaveIdException extends RuntimeException {
    public JpaCreateHaveIdException() {
        super("El objeto a crear tiene ID.");
    }
}
