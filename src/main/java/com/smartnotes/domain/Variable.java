package com.smartnotes.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "variable")
public class Variable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_variable")
    private Integer idVariable;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    private String nombre;

    @Column(name = "tipo_dato", nullable = false, length = 20)
    @NotBlank
    @Size(max = 20)
    private String tipoDato = "TEXTO";

    @Column(length = 200)
    @Size(max = 200)
    private String descripcion;
}
