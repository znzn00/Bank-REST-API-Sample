package com.zhen.backend.dto;

import com.zhen.backend.model.enumerables.TipoCuenta;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class CuentaDtoPostIn {
    @NotNull
    private Long cliente;
    @NotNull
    private TipoCuenta tipo;
    @NotNull
    private BigDecimal saldoInicial;
}
