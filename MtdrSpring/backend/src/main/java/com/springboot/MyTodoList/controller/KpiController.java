package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.dto.*;
import com.springboot.MyTodoList.service.KpiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para KPIs calculados en tiempo real desde TAREA y SPRINT.
 * Ya NO usa la tabla KPIS directamente - todo se calcula dinámicamente.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/kpis")
public class KpiController {

    private final KpiService kpiService;

    public KpiController(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    // ==================== KPIs DE PROYECTO ====================

    /**
     * Obtiene todos los KPIs de un proyecto específico.
     * Incluye: tareas, horas, sprints, velocidad, tiempo restante.
     *
     * GET /kpis/proyecto/{proyectoId}
     */
    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<KpiProyectoDto> getKpisProyecto(@PathVariable Long proyectoId) {
        KpiProyectoDto kpis = kpiService.getKpisProyecto(proyectoId);
        if (kpis == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(kpis);
    }

    /**
     * Obtiene KPIs de todos los proyectos visibles para un usuario.
     *
     * GET /kpis/usuario/{usuarioId}/proyectos
     */
    @GetMapping("/usuario/{usuarioId}/proyectos")
    public ResponseEntity<List<KpiProyectoDto>> getKpisProyectosUsuario(@PathVariable Long usuarioId) {
        List<KpiProyectoDto> kpis = kpiService.getKpisProyectosUsuario(usuarioId);
        return ResponseEntity.ok(kpis);
    }

    // ==================== KPIs DE SPRINT ====================

    /**
     * Obtiene los KPIs de un sprint específico.
     *
     * GET /kpis/sprint/{sprintId}
     */
    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<KpiSprintDto> getKpisSprint(@PathVariable Long sprintId) {
        KpiSprintDto kpis = kpiService.getKpisSprint(sprintId);
        if (kpis == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(kpis);
    }

    /**
     * Obtiene los KPIs de todos los sprints de un proyecto.
     *
     * GET /kpis/proyecto/{proyectoId}/sprints
     */
    @GetMapping("/proyecto/{proyectoId}/sprints")
    public ResponseEntity<List<KpiSprintDto>> getKpisSprintsPorProyecto(@PathVariable Long proyectoId) {
        List<KpiSprintDto> kpis = kpiService.getKpisSprintsPorProyecto(proyectoId);
        return ResponseEntity.ok(kpis);
    }

    // ==================== GRÁFICAS ====================

    /**
     * Obtiene los datos para la gráfica de burndown de un sprint.
     * Retorna puntos con: fecha, tareasRestantes, tareasCompletadas,
     * horasRestantes, horasCompletadas, idealRestante
     *
     * GET /kpis/burndown/{sprintId}
     */
    @GetMapping("/burndown/{sprintId}")
    public ResponseEntity<List<BurndownPointDto>> getBurndownSprint(@PathVariable Long sprintId) {
        List<BurndownPointDto> burndown = kpiService.getBurndownSprint(sprintId);
        return ResponseEntity.ok(burndown);
    }

    /**
     * Obtiene la velocidad histórica por sprints de un proyecto.
     * Útil para gráficas de velocidad del equipo.
     *
     * GET /kpis/velocidad/{proyectoId}
     */
    @GetMapping("/velocidad/{proyectoId}")
    public ResponseEntity<List<VelocidadSprintDto>> getVelocidadProyecto(@PathVariable Long proyectoId) {
        List<VelocidadSprintDto> velocidad = kpiService.getVelocidadProyecto(proyectoId);
        return ResponseEntity.ok(velocidad);
    }

    // ==================== KPIs DE MIEMBROS ====================

    /**
     * Obtiene los KPIs de todos los miembros de un proyecto.
     *
     * GET /kpis/proyecto/{proyectoId}/miembros
     */
    @GetMapping("/proyecto/{proyectoId}/miembros")
    public ResponseEntity<List<KpiMiembroDto>> getKpisMiembrosProyecto(@PathVariable Long proyectoId) {
        List<KpiMiembroDto> kpis = kpiService.getKpisMiembrosProyecto(proyectoId);
        return ResponseEntity.ok(kpis);
    }

    /**
     * Obtiene los KPIs de un miembro específico en un proyecto.
     *
     * GET /kpis/miembro/{odaId}/proyecto/{proyectoId}
     */
    @GetMapping("/miembro/{odaId}/proyecto/{proyectoId}")
    public ResponseEntity<KpiMiembroDto> getKpisMiembro(
            @PathVariable String odaId,
            @PathVariable Long proyectoId) {
        KpiMiembroDto kpi = kpiService.getKpisMiembro(odaId, proyectoId);
        return ResponseEntity.ok(kpi);
    }

    // ==================== RESUMEN GENERAL ====================

    /**
     * Endpoint de resumen rápido para dashboard.
     * Retorna KPIs del proyecto con información esencial.
     *
     * GET /kpis/resumen/{proyectoId}
     */
    @GetMapping("/resumen/{proyectoId}")
    public ResponseEntity<KpiProyectoDto> getResumenProyecto(@PathVariable Long proyectoId) {
        return getKpisProyecto(proyectoId);
    }
}
