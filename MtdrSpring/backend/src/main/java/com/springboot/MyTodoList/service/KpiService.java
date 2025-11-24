package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.dto.*;

import java.util.List;

/**
 * Servicio para calcular KPIs en tiempo real desde las tablas TAREA y SPRINT
 */
public interface KpiService {

    /**
     * Calcula todos los KPIs de un proyecto
     */
    KpiProyectoDto getKpisProyecto(Long proyectoId);

    /**
     * Calcula los KPIs de un sprint específico
     */
    KpiSprintDto getKpisSprint(Long sprintId);

    /**
     * Obtiene los KPIs de todos los sprints de un proyecto
     */
    List<KpiSprintDto> getKpisSprintsPorProyecto(Long proyectoId);

    /**
     * Calcula los datos para la gráfica de burndown de un sprint
     */
    List<BurndownPointDto> getBurndownSprint(Long sprintId);

    /**
     * Calcula la velocidad histórica por sprints de un proyecto
     */
    List<VelocidadSprintDto> getVelocidadProyecto(Long proyectoId);

    /**
     * Obtiene los KPIs de los miembros de un proyecto
     */
    List<KpiMiembroDto> getKpisMiembrosProyecto(Long proyectoId);

    /**
     * Obtiene los KPIs de un miembro específico en un proyecto
     */
    KpiMiembroDto getKpisMiembro(String odaId, Long proyectoId);

    /**
     * Obtiene resumen de KPIs de todos los proyectos visibles para un usuario
     */
    List<KpiProyectoDto> getKpisProyectosUsuario(Long usuarioId);

    /**
     * Obtiene todos los datos del dashboard de un proyecto en una sola llamada
     */
    com.springboot.MyTodoList.dto.DashboardDto getDashboardProyecto(Long proyectoId);
}
