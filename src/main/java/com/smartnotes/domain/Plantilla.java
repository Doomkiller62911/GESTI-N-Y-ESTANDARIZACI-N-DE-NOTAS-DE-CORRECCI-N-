package com.smartnotes.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plantilla")
public class Plantilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false, length = 4000)
    private String textoBase;

    private boolean activo = true;

    @OneToMany(mappedBy = "plantilla", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Variable> variables = new ArrayList<>();

    public Plantilla() {
    }

    public Plantilla(String nombre, String descripcion, String textoBase, boolean activo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.textoBase = textoBase;
        this.activo = activo;
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

    public String getTextoBase() {
        return textoBase;
    }

    public void setTextoBase(String textoBase) {
        this.textoBase = textoBase;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    public void addVariable(Variable variable) {
        variables.add(variable);
        variable.setPlantilla(this);
    }
}
