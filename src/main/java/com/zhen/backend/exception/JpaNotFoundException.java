package com.zhen.backend.exception;

import lombok.Getter;

@Getter
public class JpaNotFoundException extends RuntimeException {
    private final Object id;
    public JpaNotFoundException(Object id) {
        super("No se encontro la entidad con ID: "+id.toString() );
        this.id = id;
    }

    public JpaNotFoundException(String message, Object id) {
        super(message);
        this.id = id;
    }
}
