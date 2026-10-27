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
@Table(name = "categoria")
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    private String nombre;

    @Column(length = 300)
    @Size(max = 300)
    private String descripcion;

    @Column(nullable = false)
    private boolean activo = true;
}
