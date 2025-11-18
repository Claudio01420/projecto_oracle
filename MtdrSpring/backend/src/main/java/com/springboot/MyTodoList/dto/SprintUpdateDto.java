package com.springboot.MyTodoList.dto;

import java.time.LocalDate;

public class SprintUpdateDto {

    private String tituloSprint;
    private String descripcionSprint;
    private LocalDate duracion;      // fecha fin
    private Integer numero;
    private Long projectId;
    private LocalDate fechaInicio;

    public SprintUpdateDto() {}

    public String getTituloSprint() { return tituloSprint; }
    public void setTituloSprint(String tituloSprint) { this.tituloSprint = tituloSprint; }

    public String getDescripcionSprint() { return descripcionSprint; }
    public void setDescripcionSprint(String descripcionSprint) { this.descripcionSprint = descripcionSprint; }

    public LocalDate getDuracion() { return duracion; }
    public void setDuracion(LocalDate duracion) { this.duracion = duracion; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
}
