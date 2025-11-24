package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.dto.*;
import com.springboot.MyTodoList.service.KpiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para Dashboard con datos calculados en tiempo real.
 * Proporciona endpoints optimizados para vistas de dashboard.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/dashboard")
public class DashbordController {

    private final KpiService kpiService;

    public DashbordController(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    /**
     * Obtiene todos los datos del dashboard de un proyecto en una sola llamada.
     * Incluye: KPIs del proyecto, sprint activo, burndown, velocidad, miembros.
     *
     * GET /dashboard/proyecto/{proyectoId}
     */
    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<DashboardDto> getDashboardProyecto(@PathVariable Long proyectoId) {
        DashboardDto dashboard = kpiService.getDashboardProyecto(proyectoId);
        if (dashboard == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Obtiene resumen de todos los proyectos de un usuario para vista principal.
     *
     * GET /dashboard/usuario/{usuarioId}
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<KpiProyectoDto>> getDashboardUsuario(@PathVariable Long usuarioId) {
        List<KpiProyectoDto> proyectos = kpiService.getKpisProyectosUsuario(usuarioId);
        return ResponseEntity.ok(proyectos);
    }

    /**
     * Obtiene el burndown chart de un sprint.
     *
     * GET /dashboard/burndown/{sprintId}
     */
    @GetMapping("/burndown/{sprintId}")
    public ResponseEntity<List<BurndownPointDto>> getBurndown(@PathVariable Long sprintId) {
        List<BurndownPointDto> burndown = kpiService.getBurndownSprint(sprintId);
        return ResponseEntity.ok(burndown);
    }

    /**
     * Obtiene la gráfica de velocidad de un proyecto.
     *
     * GET /dashboard/velocidad/{proyectoId}
     */
    @GetMapping("/velocidad/{proyectoId}")
    public ResponseEntity<List<VelocidadSprintDto>> getVelocidad(@PathVariable Long proyectoId) {
        List<VelocidadSprintDto> velocidad = kpiService.getVelocidadProyecto(proyectoId);
        return ResponseEntity.ok(velocidad);
    }

    /**
     * Obtiene los KPIs del equipo de un proyecto.
     *
     * GET /dashboard/equipo/{proyectoId}
     */
    @GetMapping("/equipo/{proyectoId}")
    public ResponseEntity<List<KpiMiembroDto>> getEquipo(@PathVariable Long proyectoId) {
        List<KpiMiembroDto> equipo = kpiService.getKpisMiembrosProyecto(proyectoId);
        return ResponseEntity.ok(equipo);
    }

    /**
     * Obtiene información del sprint activo de un proyecto.
     *
     * GET /dashboard/sprint-activo/{proyectoId}
     */
    @GetMapping("/sprint-activo/{proyectoId}")
    public ResponseEntity<KpiSprintDto> getSprintActivo(@PathVariable Long proyectoId) {
        DashboardDto dashboard = kpiService.getDashboardProyecto(proyectoId);
        if (dashboard == null || dashboard.getSprintActivo() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dashboard.getSprintActivo());
    }

    /**
     * Obtiene todos los sprints de un proyecto con sus KPIs.
     *
     * GET /dashboard/sprints/{proyectoId}
     */
    @GetMapping("/sprints/{proyectoId}")
    public ResponseEntity<List<KpiSprintDto>> getSprints(@PathVariable Long proyectoId) {
        List<KpiSprintDto> sprints = kpiService.getKpisSprintsPorProyecto(proyectoId);
        return ResponseEntity.ok(sprints);
    }

    // ==================== ENDPOINTS LEGACY (compatibilidad) ====================

    /**
     * Endpoint legacy - redirige a /dashboard/proyecto/{id}
     * Mantiene compatibilidad con código existente que usaba /dashbord
     */
    @GetMapping("/{id}")
    public ResponseEntity<DashboardDto> legacyGetOne(@PathVariable Long id) {
        return getDashboardProyecto(id);
    }
}
