package com.zhen.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhen.backend.model.enumerables.EstadoCliente;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@PrimaryKeyJoinColumn(name="id")
//@ToString
public class Cliente extends Persona{

    //    requeridos
    @Column(nullable = false, unique = true)
    private String clienteId;
    private String contrasena;
    private EstadoCliente estado;

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
