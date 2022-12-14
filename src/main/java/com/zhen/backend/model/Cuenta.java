package com.zhen.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhen.backend.model.enumerables.EstadoCuenta;
import com.zhen.backend.model.enumerables.TipoCuenta;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Cuenta {
    // requeridos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numeroCuenta;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

//    @JsonIgnore
    @Column(name = "cliente_id", insertable = false, updatable = false)
    private Long clienteId;

    private TipoCuenta tipo;
    @Column(updatable = false)
    private BigDecimal saldoInicial;
    private EstadoCuenta estado;

    // tracking de accion
    @CreationTimestamp
    @JsonIgnore
    @Column(name = "creation_timestamp", updatable = false)
    private LocalDateTime creationTimestamp;

    @UpdateTimestamp
    @JsonIgnore
    @Column(name = "update_timestamp")
    private LocalDateTime updateTimestamp;

}
