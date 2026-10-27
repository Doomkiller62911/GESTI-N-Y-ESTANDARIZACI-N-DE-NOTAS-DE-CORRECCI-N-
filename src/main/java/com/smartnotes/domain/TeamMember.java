package com.smartnotes.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "team_member")
public class TeamMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_team_member")
    private Integer idTeamMember;

    @Column(name = "numero_empleado", unique = true, nullable = false, length = 20)
    @NotBlank
    @Size(max = 20)
    private String numeroEmpleado;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String nombre;

    @Column(length = 50)
    @Size(max = 50)
    private String puesto;

    @Column(length = 50)
    @Size(max = 50)
    private String area;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", insertable = false, updatable = false)
    private LocalDateTime fechaModificacion;
}
