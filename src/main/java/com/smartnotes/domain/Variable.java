package com.smartnotes.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "variable")
public class Variable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clave;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String tipo = "texto";

    @Column(nullable = false)
    private Integer orden = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantilla_id", nullable = false)
    private Plantilla plantilla;

    public Variable() {
    }

    public Variable(String clave, String nombre, String tipo, Integer orden, Plantilla plantilla) {
        this.clave = clave;
        this.nombre = nombre;
        this.tipo = tipo;
        this.orden = orden;
        this.plantilla = plantilla;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Plantilla getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(Plantilla plantilla) {
        this.plantilla = plantilla;
    }
}
