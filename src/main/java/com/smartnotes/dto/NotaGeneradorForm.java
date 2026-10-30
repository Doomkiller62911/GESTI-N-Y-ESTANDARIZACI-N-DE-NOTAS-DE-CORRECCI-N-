package com.smartnotes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class NotaGeneradorForm {

    @NotNull(message = "Debe seleccionar una plantilla.")
    private Long plantillaId;

    @NotNull(message = "Debe seleccionar un miembro del equipo.")
    private Long teamMemberId;

    @NotNull(message = "La fecha de inicio es requerida.")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha final es requerida.")
    private LocalDate fechaFin;

    @NotBlank(message = "La descripción no puede estar vacía.")
    private String descripcion;

    @NotBlank(message = "La corrección no puede estar vacía.")
    private String correccion;

    private String evidencia;

    private boolean guardarComoBorrador;

    public NotaGeneradorForm() {
    }

    public Long getPlantillaId() {
        return plantillaId;
    }

    public void setPlantillaId(Long plantillaId) {
        this.plantillaId = plantillaId;
    }

    public Long getTeamMemberId() {
        return teamMemberId;
    }

    public void setTeamMemberId(Long teamMemberId) {
        this.teamMemberId = teamMemberId;
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

    public String getEvidencia() {
        return evidencia;
    }

    public void setEvidencia(String evidencia) {
        this.evidencia = evidencia;
    }

    public boolean isGuardarComoBorrador() {
        return guardarComoBorrador;
    }

    public void setGuardarComoBorrador(boolean guardarComoBorrador) {
        this.guardarComoBorrador = guardarComoBorrador;
    }
}
