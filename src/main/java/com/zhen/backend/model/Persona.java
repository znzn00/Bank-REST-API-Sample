package com.zhen.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhen.backend.model.enumerables.Genero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    // requeridos
    private String nombre;
    private String apellido;
    private Genero genero;
    private String identificacion;
    private String direccion;
    private String telefono;
    private Date fechaNacimiento;

    @Transient
    private int edad;



    // tracking de accion
    @CreationTimestamp
    @JsonIgnore
    @Column(name = "creation_timestamp", updatable = false)
    private LocalDateTime creationTimestamp;

    @UpdateTimestamp
    @JsonIgnore
    @Column(name = "update_timestamp")
    private LocalDateTime updateTimestamp;

    @PostLoad
    public void calcularEdad() {
        this.edad = Period.between(this.fechaNacimiento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).getYears();
    }
}
