package com.zhen.backend.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MovimientoLimitPassException extends RuntimeException {
    BigDecimal exceso;

    public MovimientoLimitPassException(String message, BigDecimal exceso) {
        super(message);
        this.exceso = exceso;
    }
}
