package com.smartnotes.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "plantilla")
public class Plantilla implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plantilla")
    private Integer idPlantilla;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(unique = true, nullable = false, length = 100)
    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres.")
    private String nombre;

    @Column(name = "tipo_asociado", length = 100)
    @Size(max = 100)
    private String tipoAsociado;

    @Column(length = 300)
    @Size(max = 300)
    private String descripcion;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "El contenido no puede estar vacío.")
    private String contenido;

    @Column(name = "requiere_evidencia", nullable = false)
    private boolean requiereEvidencia = false;

    @Column(nullable = false)
    private boolean activo = true;

    // Último usuario que modificó la plantilla
    @ManyToOne
    @JoinColumn(name = "id_usuario_modificacion")
    private Usuario usuarioModificacion;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", insertable = false, updatable = false)
    private LocalDateTime fechaModificacion;

    // Perfiles autorizados para usar la plantilla (tabla plantilla_rol)
    @ManyToMany
    @JoinTable(name = "plantilla_rol",
            joinColumns = @JoinColumn(name = "id_plantilla"),
            inverseJoinColumns = @JoinColumn(name = "id_rol"))
    private Set<Rol> roles = new HashSet<>();

    // Variables que debe llenar el usuario al usar la plantilla
    @OneToMany(mappedBy = "plantilla")
    @OrderBy("orden")
    private List<PlantillaVariable> variables = new ArrayList<>();
}
