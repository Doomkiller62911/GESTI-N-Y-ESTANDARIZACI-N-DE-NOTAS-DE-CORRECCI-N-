package com.smartnotes.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @NotNull(message = "Selecciona una categoría.")
    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(
            max = 100,
            message = "El nombre no puede superar 100 caracteres."
    )
    @Column(unique = true, nullable = false, length = 100)
    private String nombre;

    @Size(
            max = 100,
            message = "El tipo asociado no puede superar 100 caracteres."
    )
    @Column(name = "tipo_asociado", length = 100)
    private String tipoAsociado;

    @NotBlank(message = "La descripción es obligatoria.")
    @Size(
            max = 300,
            message = "La descripción no puede superar 300 caracteres."
    )
    @Column(length = 300)
    private String descripcion;

    @NotBlank(message = "El contenido es obligatorio.")
    @Size(
            max = 10000,
            message = "El contenido no puede superar 10000 caracteres."
    )
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "requiere_evidencia", nullable = false)
    private boolean requiereEvidencia = false;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "id_usuario_modificacion")
    private Usuario usuarioModificacion;

    @Column(
            name = "fecha_creacion",
            insertable = false,
            updatable = false
    )
    private LocalDateTime fechaCreacion;

    @Column(
            name = "fecha_modificacion",
            insertable = false,
            updatable = false
    )
    private LocalDateTime fechaModificacion;

    @ManyToMany
    @JoinTable(
            name = "plantilla_rol",
            joinColumns = @JoinColumn(name = "id_plantilla"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Rol> roles = new HashSet<>();

    @OneToMany(mappedBy = "plantilla")
    @OrderBy("orden")
    private List<PlantillaVariable> variables = new ArrayList<>();
}