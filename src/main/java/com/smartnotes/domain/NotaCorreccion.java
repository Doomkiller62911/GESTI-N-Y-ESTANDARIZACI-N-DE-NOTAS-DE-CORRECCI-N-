package com.smartnotes.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "nota_correccion")
public class NotaCorreccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plantilla_id", nullable = false)
    private Plantilla plantilla;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_member_id", nullable = false)
    private TeamMember teamMember;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(length = 2000)
    private String descripcion;

    @Column(length = 2000)
    private String correccion;

    @Column(length = 5000)
    private String contenidoGenerado;

    @Column(length = 2000)
    private String evidencias;

    @Column(nullable = false)
    private String estado = "BORRADOR";

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evidencia> evidenciasAdjuntas = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.fechaCreacion = now;
        this.fechaActualizacion = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public NotaCorreccion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Plantilla getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(Plantilla plantilla) {
        this.plantilla = plantilla;
    }

    public TeamMember getTeamMember() {
        return teamMember;
    }

    public void setTeamMember(TeamMember teamMember) {
        this.teamMember = teamMember;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCorreccion() {
        return correccion;
    }

    public void setCorreccion(String correccion) {
        this.correccion = correccion;
    }

    public String getContenidoGenerado() {
        return contenidoGenerado;
    }

    public void setContenidoGenerado(String contenidoGenerado) {
        this.contenidoGenerado = contenidoGenerado;
    }

    public String getEvidencias() {
        return evidencias;
    }

    public void setEvidencias(String evidencias) {
        this.evidencias = evidencias;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public List<Evidencia> getEvidenciasAdjuntas() {
        return evidenciasAdjuntas;
    }

    public void setEvidenciasAdjuntas(List<Evidencia> evidenciasAdjuntas) {
        this.evidenciasAdjuntas = evidenciasAdjuntas;
    }

    public void addEvidencia(Evidencia evidencia) {
        evidenciasAdjuntas.add(evidencia);
        evidencia.setNota(this);
    }
}
