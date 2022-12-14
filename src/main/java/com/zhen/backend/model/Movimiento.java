package com.zhen.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zhen.backend.model.enumerables.EstadoMovimiento;
import com.zhen.backend.model.enumerables.TipoMovimiento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
//    @Column(updatable = false)
    @JoinColumn(name = "cuenta_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Cuenta cuenta;

//    @JsonIgnore
    @Column(name = "cuenta_id", insertable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long cuentaId;

    // requeridos
    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo;


    private EstadoMovimiento estado;

    @Transient
    private EstadoMovimiento estadoPasado;

    @PostLoad
    public void marcaEstadoPasado() {
        this.estadoPasado = this.estado;
    }

    @Column(updatable = false)
    private BigDecimal valor;
//    @Column(updatable = false)
    private BigDecimal saldo;

    private String descripcion;

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
