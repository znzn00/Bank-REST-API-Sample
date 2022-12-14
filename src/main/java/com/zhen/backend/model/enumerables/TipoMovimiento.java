package com.zhen.backend.model.enumerables;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoMovimiento {
//    Negativos
    RETIRO(Orientacion.DEBITO),
    TRANSFERENCIA(Orientacion.DEBITO),
    PAGO(Orientacion.DEBITO),
//    Positivos
    DEPOSITO(Orientacion.CREDITO),
//    Ambos
    AJUSTE(Orientacion.AMBOS);

    public enum Orientacion {
        CREDITO,
        DEBITO,
        AMBOS
    }

    private Orientacion orientacion;
}
