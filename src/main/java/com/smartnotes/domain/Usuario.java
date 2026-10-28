package com.smartnotes.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    // Cada usuario tiene un único perfil (rol)
    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(unique = true, nullable = false, length = 30)
    @NotBlank
    @Size(max = 30)
    private String username;

    @Column(nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    private String nombre;

    @Column(name = "apellido_paterno", nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 50)
    @Size(max = 50)
    private String apellidoMaterno;

    @Column(length = 60)
    @Size(max = 60)
    private String puesto;

    @Column(unique = true, nullable = false, length = 100)
    @NotBlank
    @Email
    @Size(max = 100)
    private String correo;

    @Column(nullable = false, length = 255)
    @NotBlank
    private String password;

    @Column(nullable = false)
    private boolean activo = true;

    // Las fechas las genera la base de datos (DEFAULT CURRENT_TIMESTAMP)
    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", insertable = false, updatable = false)
    private LocalDateTime fechaModificacion;
}
