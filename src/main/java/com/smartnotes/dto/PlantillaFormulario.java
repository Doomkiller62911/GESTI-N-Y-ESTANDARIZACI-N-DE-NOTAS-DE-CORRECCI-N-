package com.smartnotes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class PlantillaFormulario {

    @Positive(message = "El identificador no es válido.")
    private Integer idPlantilla;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(
            max = 100,
            message = "El nombre no puede superar 100 caracteres."
    )
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria.")
    @Size(
            max = 300,
            message = "La descripción no puede superar 300 caracteres."
    )
    private String descripcion;

    @Size(
            max = 100,
            message = "El tipo asociado no puede superar 100 caracteres."
    )
    private String tipoAsociado;

    @NotNull(message = "Selecciona una categoría.")
    @Positive(message = "La categoría no es válida.")
    private Integer idCategoria;

    @NotEmpty(message = "Selecciona al menos un perfil autorizado.")
    @Size(
            max = 50,
            message = "Se permiten hasta 50 perfiles autorizados."
    )
    private Set<@NotNull @Positive Integer> idsRoles = new HashSet<>();

    @NotBlank(message = "El contenido es obligatorio.")
    @Size(
            max = 10000,
            message = "El contenido no puede superar 10000 caracteres."
    )
    private String contenido;

    private boolean activo = true;

    private boolean requiereEvidencia;

    @NotNull(message = "La lista de variables no es válida.")
    @Valid
    @Size(
            max = 50,
            message = "Se permiten hasta 50 variables."
    )
    private List<@NotNull VariableFormulario> variables =
            new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class VariableFormulario {

        @NotBlank(message = "El nombre de la variable es obligatorio.")
        @Pattern(
                regexp = "[A-Z][A-Z0-9_ ]{0,49}",
                message = "El nombre debe empezar por una letra "
                        + "mayúscula y usar mayúsculas, números, "
                        + "espacios o guion bajo."
        )
        private String nombre;

        @NotBlank(message = "La descripción de la variable es obligatoria.")
        @Size(
                max = 200,
                message = "La descripción de la variable "
                        + "no puede superar 200 caracteres."
        )
        private String descripcion;

        private boolean obligatoria = true;
    }
}