package com.smartnotes.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "evidencia")
public class Evidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String detalle;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_id", nullable = false)
    private NotaCorreccion nota;

    public Evidencia() {
    }

    public Evidencia(String nombre, String detalle, String url, NotaCorreccion nota) {
        this.nombre = nombre;
        this.detalle = detalle;
        this.url = url;
        this.nota = nota;
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

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public NotaCorreccion getNota() {
        return nota;
    }

    public void setNota(NotaCorreccion nota) {
        this.nota = nota;
    }
}
