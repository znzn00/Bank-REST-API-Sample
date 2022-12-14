package com.zhen.backend.model.enumerables;

import com.zhen.backend.model.enumerables.convertidores.BaseConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Converter;

@AllArgsConstructor
@Getter
public enum EstadoMovimiento  implements Etiquetable{
    COMPLETADO("C"),
    PENDIENTE("P"),
    CANCELADO("S"),
    REVERSADO("R");


    private final String etiqueta;

    @Converter(autoApply = true)
    public static class Convertidor extends BaseConverter<EstadoMovimiento> {}
}
