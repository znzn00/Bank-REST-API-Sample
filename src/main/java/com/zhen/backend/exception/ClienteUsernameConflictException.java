package com.zhen.backend.exception;


public class ClienteUsernameConflictException extends RuntimeException {
    public ClienteUsernameConflictException() {
        super("El clienteID ya se encuentra con otro.");
    }
}
