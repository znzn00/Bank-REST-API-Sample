package com.zhen.backend.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ClienteHaveCuentaException extends RuntimeException {
    private List<Long> cuentas;

    public ClienteHaveCuentaException(Long id, List<Long> cuentas) {
        super("El cliente con id \"" + id + "\" tiene cuentas.");
        this.cuentas = cuentas;
    }
}
