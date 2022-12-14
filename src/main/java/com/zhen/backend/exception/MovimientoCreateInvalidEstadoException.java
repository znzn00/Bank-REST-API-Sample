package com.zhen.backend.exception;

import com.zhen.backend.model.enumerables.EstadoMovimiento;
import lombok.Getter;

@Getter
public class MovimientoCreateInvalidEstadoException extends RuntimeException {
    private EstadoMovimiento estadoMovimiento;

    public MovimientoCreateInvalidEstadoException(EstadoMovimiento estadoMovimiento) {
        super("No se puede crear un movimiento inicial con el estado: "+estadoMovimiento.toString());
        this.estadoMovimiento = estadoMovimiento;

    }
}
