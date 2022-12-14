package com.zhen.backend.exception;

public class CuentaIsNotActiveException extends RuntimeException{
    public CuentaIsNotActiveException() {
        super("La cuenta que se le quiere realizar algún cambio no esta activo.");
    }
}
