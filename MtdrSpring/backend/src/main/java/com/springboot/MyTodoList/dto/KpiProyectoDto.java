package com.springboot.MyTodoList.dto;

/**
 * DTO con los KPIs calculados de un proyecto
 */
public class KpiProyectoDto {

    private Long proyectoId;
    private String nombreProyecto;

    // Métricas de tareas
    private long totalTareas;
    private long tareasCompletadas;
    private long tareasPendientes;
    private double porcentajeCompletado;

    // Métricas de horas
    private double horasEstimadas;
    private double horasReales;
    private double eficienciaHoras; // (estimadas / reales) * 100

    // Métricas de sprints
    private int totalSprints;
    private int sprintsCompletados;
    private double velocidadPromedio; // tareas completadas por sprint

    // Métricas de tiempo
    private long diasRestantes;
    private double porcentajeTiempoTranscurrido;

    public KpiProyectoDto() {}

    // Getters y Setters
    public Long getProyectoId() { return proyectoId; }
    public void setProyectoId(Long proyectoId) { this.proyectoId = proyectoId; }

    public String getNombreProyecto() { return nombreProyecto; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }

    public long getTotalTareas() { return totalTareas; }
    public void setTotalTareas(long totalTareas) { this.totalTareas = totalTareas; }

    public long getTareasCompletadas() { return tareasCompletadas; }
    public void setTareasCompletadas(long tareasCompletadas) { this.tareasCompletadas = tareasCompletadas; }

    public long getTareasPendientes() { return tareasPendientes; }
    public void setTareasPendientes(long tareasPendientes) { this.tareasPendientes = tareasPendientes; }

    public double getPorcentajeCompletado() { return porcentajeCompletado; }
    public void setPorcentajeCompletado(double porcentajeCompletado) { this.porcentajeCompletado = porcentajeCompletado; }

    public double getHorasEstimadas() { return horasEstimadas; }
    public void setHorasEstimadas(double horasEstimadas) { this.horasEstimadas = horasEstimadas; }

    public double getHorasReales() { return horasReales; }
    public void setHorasReales(double horasReales) { this.horasReales = horasReales; }

    public double getEficienciaHoras() { return eficienciaHoras; }
    public void setEficienciaHoras(double eficienciaHoras) { this.eficienciaHoras = eficienciaHoras; }

    public int getTotalSprints() { return totalSprints; }
    public void setTotalSprints(int totalSprints) { this.totalSprints = totalSprints; }

    public int getSprintsCompletados() { return sprintsCompletados; }
    public void setSprintsCompletados(int sprintsCompletados) { this.sprintsCompletados = sprintsCompletados; }

    public double getVelocidadPromedio() { return velocidadPromedio; }
    public void setVelocidadPromedio(double velocidadPromedio) { this.velocidadPromedio = velocidadPromedio; }

    public long getDiasRestantes() { return diasRestantes; }
    public void setDiasRestantes(long diasRestantes) { this.diasRestantes = diasRestantes; }

    public double getPorcentajeTiempoTranscurrido() { return porcentajeTiempoTranscurrido; }
    public void setPorcentajeTiempoTranscurrido(double porcentajeTiempoTranscurrido) { this.porcentajeTiempoTranscurrido = porcentajeTiempoTranscurrido; }
}
