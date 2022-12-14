package com.zhen.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zhen.backend.model.Cliente;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReporteEstadoDeCuentas {
    private Cliente cliente;
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;
    private List<CuentaDtoEstado> cuentas;
}
