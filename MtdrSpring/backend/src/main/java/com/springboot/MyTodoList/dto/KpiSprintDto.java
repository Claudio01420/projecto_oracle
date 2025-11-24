package com.springboot.MyTodoList.dto;

import java.time.LocalDate;

/**
 * DTO con los KPIs calculados de un sprint específico
 */
public class KpiSprintDto {

    private Long sprintId;
    private String tituloSprint;
    private Integer numeroSprint;
    private Long proyectoId;

    // Fechas
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private long diasTotales;
    private long diasRestantes;
    private boolean activo;

    // Métricas de tareas
    private long totalTareas;
    private long tareasCompletadas;
    private long tareasPendientes;
    private long tareasEnProgreso;
    private double porcentajeCompletado;

    // Métricas de horas
    private double horasEstimadas;
    private double horasReales;
    private double eficienciaHoras;

    // Velocidad
    private double tareasCompletadasPorDia;
    private double horasPromedioTarea;

    // Cumplimiento
    private double cumplimientoSprint; // % de tareas completadas a tiempo

    public KpiSprintDto() {}

    // Getters y Setters
    public Long getSprintId() { return sprintId; }
    public void setSprintId(Long sprintId) { this.sprintId = sprintId; }

    public String getTituloSprint() { return tituloSprint; }
    public void setTituloSprint(String tituloSprint) { this.tituloSprint = tituloSprint; }

    public Integer getNumeroSprint() { return numeroSprint; }
    public void setNumeroSprint(Integer numeroSprint) { this.numeroSprint = numeroSprint; }

    public Long getProyectoId() { return proyectoId; }
    public void setProyectoId(Long proyectoId) { this.proyectoId = proyectoId; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public long getDiasTotales() { return diasTotales; }
    public void setDiasTotales(long diasTotales) { this.diasTotales = diasTotales; }

    public long getDiasRestantes() { return diasRestantes; }
    public void setDiasRestantes(long diasRestantes) { this.diasRestantes = diasRestantes; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public long getTotalTareas() { return totalTareas; }
    public void setTotalTareas(long totalTareas) { this.totalTareas = totalTareas; }

    public long getTareasCompletadas() { return tareasCompletadas; }
    public void setTareasCompletadas(long tareasCompletadas) { this.tareasCompletadas = tareasCompletadas; }

    public long getTareasPendientes() { return tareasPendientes; }
    public void setTareasPendientes(long tareasPendientes) { this.tareasPendientes = tareasPendientes; }

    public long getTareasEnProgreso() { return tareasEnProgreso; }
    public void setTareasEnProgreso(long tareasEnProgreso) { this.tareasEnProgreso = tareasEnProgreso; }

    public double getPorcentajeCompletado() { return porcentajeCompletado; }
    public void setPorcentajeCompletado(double porcentajeCompletado) { this.porcentajeCompletado = porcentajeCompletado; }

    public double getHorasEstimadas() { return horasEstimadas; }
    public void setHorasEstimadas(double horasEstimadas) { this.horasEstimadas = horasEstimadas; }

    public double getHorasReales() { return horasReales; }
    public void setHorasReales(double horasReales) { this.horasReales = horasReales; }

    public double getEficienciaHoras() { return eficienciaHoras; }
    public void setEficienciaHoras(double eficienciaHoras) { this.eficienciaHoras = eficienciaHoras; }

    public double getTareasCompletadasPorDia() { return tareasCompletadasPorDia; }
    public void setTareasCompletadasPorDia(double tareasCompletadasPorDia) { this.tareasCompletadasPorDia = tareasCompletadasPorDia; }

    public double getHorasPromedioTarea() { return horasPromedioTarea; }
    public void setHorasPromedioTarea(double horasPromedioTarea) { this.horasPromedioTarea = horasPromedioTarea; }

    public double getCumplimientoSprint() { return cumplimientoSprint; }
    public void setCumplimientoSprint(double cumplimientoSprint) { this.cumplimientoSprint = cumplimientoSprint; }
}
