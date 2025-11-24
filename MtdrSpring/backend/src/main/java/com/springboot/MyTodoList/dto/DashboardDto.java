package com.springboot.MyTodoList.dto;

import java.util.List;

/**
 * DTO con todos los datos necesarios para el dashboard
 */
public class DashboardDto {

    // Información del proyecto
    private Long proyectoId;
    private String nombreProyecto;
    private String estado;

    // Sprint activo
    private KpiSprintDto sprintActivo;

    // KPIs generales del proyecto
    private KpiProyectoDto kpisProyecto;

    // Datos para gráficas
    private List<BurndownPointDto> burndown;
    private List<VelocidadSprintDto> velocidad;

    // KPIs de miembros
    private List<KpiMiembroDto> miembros;

    // Resumen de sprints
    private List<KpiSprintDto> sprints;

    public DashboardDto() {}

    // Getters y Setters
    public Long getProyectoId() { return proyectoId; }
    public void setProyectoId(Long proyectoId) { this.proyectoId = proyectoId; }

    public String getNombreProyecto() { return nombreProyecto; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public KpiSprintDto getSprintActivo() { return sprintActivo; }
    public void setSprintActivo(KpiSprintDto sprintActivo) { this.sprintActivo = sprintActivo; }

    public KpiProyectoDto getKpisProyecto() { return kpisProyecto; }
    public void setKpisProyecto(KpiProyectoDto kpisProyecto) { this.kpisProyecto = kpisProyecto; }

    public List<BurndownPointDto> getBurndown() { return burndown; }
    public void setBurndown(List<BurndownPointDto> burndown) { this.burndown = burndown; }

    public List<VelocidadSprintDto> getVelocidad() { return velocidad; }
    public void setVelocidad(List<VelocidadSprintDto> velocidad) { this.velocidad = velocidad; }

    public List<KpiMiembroDto> getMiembros() { return miembros; }
    public void setMiembros(List<KpiMiembroDto> miembros) { this.miembros = miembros; }

    public List<KpiSprintDto> getSprints() { return sprints; }
    public void setSprints(List<KpiSprintDto> sprints) { this.sprints = sprints; }
}
