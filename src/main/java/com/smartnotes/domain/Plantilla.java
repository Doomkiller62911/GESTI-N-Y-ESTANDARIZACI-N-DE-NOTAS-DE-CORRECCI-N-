package com.smartnotes.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class Plantilla {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(
            max = 150,
            message = "El nombre no puede superar 150 caracteres."
    )
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria.")
    @Size(
            max = 500,
            message = "La descripción no puede superar 500 caracteres."
    )
    private String descripcion;

    @Size(
            max = 150,
            message = "El tipo asociado no puede superar 150 caracteres."
    )
    private String tipoAsociado;

    @NotBlank(message = "Selecciona una categoría.")
    @Pattern(
            regexp = "Documentos|Equipos|N/A",
            message = "La categoría seleccionada no es válida."
    )
    private String categoria;

    @NotBlank(message = "Selecciona un perfil autorizado.")
    @Pattern(
            regexp = "Supervisor|Líder|QC",
            message = "El perfil seleccionado no es válido."
    )
    private String perfil;

    @NotBlank(message = "El contenido es obligatorio.")
    @Size(
            max = 10000,
            message = "El contenido no puede superar 10000 caracteres."
    )
    private String contenido;

    private boolean activa;

    @Valid
    @Size(
            max = 50,
            message = "No se permiten más de 50 variables."
    )
    private List<Variable> variables = new ArrayList<>();

    public Plantilla() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoAsociado() {
        return tipoAsociado;
    }

    public void setTipoAsociado(String tipoAsociado) {
        this.tipoAsociado = tipoAsociado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables == null
                ? new ArrayList<>()
                : new ArrayList<>(variables);
    }

    public static class Variable {

        @NotBlank(message = "El nombre de la variable es obligatorio.")
        @Pattern(
                regexp = "[A-Z][A-Z0-9_ ]{0,49}",
                message = "Usa mayúsculas, números, espacios "
                + "o guion bajo en la variable."
        )
        private String nombre;

        @NotBlank(message = "La descripción de la variable es obligatoria.")
        @Size(
                max = 250,
                message = "La descripción no puede superar 250 caracteres."
        )
        private String descripcion;

        private boolean obligatoria;

        public Variable() {
        }

        public Variable(
                String nombre,
                String descripcion,
                boolean obligatoria) {

            this.nombre = nombre;
            this.descripcion = descripcion;
            this.obligatoria = obligatoria;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public boolean isObligatoria() {
            return obligatoria;
        }

        public void setObligatoria(boolean obligatoria) {
            this.obligatoria = obligatoria;
        }
    }
}
