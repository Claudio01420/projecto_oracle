package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.dto.BurndownPointDto;
import com.springboot.MyTodoList.dto.DashboardDto;
import com.springboot.MyTodoList.dto.KpiMiembroDto;
import com.springboot.MyTodoList.dto.KpiProyectoDto;
import com.springboot.MyTodoList.dto.KpiSprintDto;
import com.springboot.MyTodoList.dto.VelocidadSprintDto;
import com.springboot.MyTodoList.model.Proyecto;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.model.UsuarioEquipo;
import com.springboot.MyTodoList.repository.ProyectoRepository;
import com.springboot.MyTodoList.repository.SprintRepository;
import com.springboot.MyTodoList.repository.TareaRepository;
import com.springboot.MyTodoList.repository.UsuarioEquipoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KpiServiceImpl implements KpiService {

    private final TareaRepository tareaRepo;
    private final SprintRepository sprintRepo;
    private final ProyectoRepository proyectoRepo;
    private final UsuarioEquipoRepository usuarioEquipoRepo;

    public KpiServiceImpl(TareaRepository tareaRepo,
                          SprintRepository sprintRepo,
                          ProyectoRepository proyectoRepo,
                          UsuarioEquipoRepository usuarioEquipoRepo) {
        this.tareaRepo = tareaRepo;
        this.sprintRepo = sprintRepo;
        this.proyectoRepo = proyectoRepo;
        this.usuarioEquipoRepo = usuarioEquipoRepo;
    }

    @Override
    public KpiProyectoDto getKpisProyecto(Long proyectoId) {
        Proyecto proyecto = proyectoRepo.findById(proyectoId).orElse(null);
        if (proyecto == null) {
            return null;
        }

        KpiProyectoDto dto = new KpiProyectoDto();
        dto.setProyectoId(proyectoId);
        dto.setNombreProyecto(proyecto.getNombreProyecto());

        // Métricas de tareas
        long totalTareas = tareaRepo.countByProjectId(proyectoId);
        long tareasCompletadas = tareaRepo.countCompletedByProjectId(proyectoId);
        long tareasPendientes = totalTareas - tareasCompletadas;
        double porcentaje = totalTareas == 0 ? 0 : (double) tareasCompletadas / totalTareas * 100;

        dto.setTotalTareas(totalTareas);
        dto.setTareasCompletadas(tareasCompletadas);
        dto.setTareasPendientes(tareasPendientes);
        dto.setPorcentajeCompletado(redondear(porcentaje));

        // Métricas de horas
        Double horasEstimadas = tareaRepo.sumEstimatedHoursByProjectId(proyectoId);
        Double horasReales = tareaRepo.sumRealHoursByProjectId(proyectoId);
        horasEstimadas = horasEstimadas != null ? horasEstimadas : 0.0;
        horasReales = horasReales != null ? horasReales : 0.0;

        double eficiencia = horasReales == 0 ? 100 : (horasEstimadas / horasReales) * 100;

        dto.setHorasEstimadas(redondear(horasEstimadas));
        dto.setHorasReales(redondear(horasReales));
        dto.setEficienciaHoras(redondear(eficiencia));

        // Métricas de sprints
        LocalDate hoy = LocalDate.now();
        int totalSprints = (int) sprintRepo.countByProjectId(proyectoId);
        int sprintsCompletados = (int) sprintRepo.countCompletedByProjectId(proyectoId, hoy);

        dto.setTotalSprints(totalSprints);
        dto.setSprintsCompletados(sprintsCompletados);

        // Velocidad promedio (tareas completadas por sprint)
        double velocidad = sprintsCompletados == 0 ? 0 : (double) tareasCompletadas / sprintsCompletados;
        dto.setVelocidadPromedio(redondear(velocidad));

        // Métricas de tiempo
        if (proyecto.getFechaFin() != null) {
            long diasRestantes = ChronoUnit.DAYS.between(hoy, proyecto.getFechaFin());
            dto.setDiasRestantes(Math.max(0, diasRestantes));

            if (proyecto.getFechaInicio() != null) {
                long diasTotales = ChronoUnit.DAYS.between(proyecto.getFechaInicio(), proyecto.getFechaFin());
                long diasTranscurridos = ChronoUnit.DAYS.between(proyecto.getFechaInicio(), hoy);
                double porcentajeTiempo = diasTotales == 0 ? 100 : (double) diasTranscurridos / diasTotales * 100;
                dto.setPorcentajeTiempoTranscurrido(redondear(Math.min(100, Math.max(0, porcentajeTiempo))));
            }
        } else {
            dto.setDiasRestantes(-1); // Sin fecha fin definida
            dto.setPorcentajeTiempoTranscurrido(0);
        }

        return dto;
    }

    @Override
    public KpiSprintDto getKpisSprint(Long sprintId) {
        Sprint sprint = sprintRepo.findById(sprintId).orElse(null);
        if (sprint == null) {
            return null;
        }

        return calcularKpiSprint(sprint);
    }

    @Override
    public List<KpiSprintDto> getKpisSprintsPorProyecto(Long proyectoId) {
        List<Sprint> sprints = sprintRepo.findByProjectIdOrderByNumeroAsc(proyectoId);
        return sprints.stream()
                .map(this::calcularKpiSprint)
                .collect(Collectors.toList());
    }

    private KpiSprintDto calcularKpiSprint(Sprint sprint) {
        KpiSprintDto dto = new KpiSprintDto();
        dto.setSprintId(sprint.getId());
        dto.setTituloSprint(sprint.getTituloSprint());
        dto.setNumeroSprint(sprint.getNumero());
        dto.setProyectoId(sprint.getProjectId());
        dto.setFechaInicio(sprint.getFechaInicio());
        dto.setFechaFin(sprint.getDuracion());

        LocalDate hoy = LocalDate.now();
        String sprintIdStr = String.valueOf(sprint.getId());

        // Calcular días
        if (sprint.getFechaInicio() != null && sprint.getDuracion() != null) {
            long diasTotales = ChronoUnit.DAYS.between(sprint.getFechaInicio(), sprint.getDuracion());
            long diasRestantes = ChronoUnit.DAYS.between(hoy, sprint.getDuracion());

            dto.setDiasTotales(Math.max(0, diasTotales));
            dto.setDiasRestantes(Math.max(0, diasRestantes));

            // Determinar si está activo
            boolean activo = !hoy.isBefore(sprint.getFechaInicio()) && !hoy.isAfter(sprint.getDuracion());
            dto.setActivo(activo);
        }

        // Métricas de tareas
        long totalTareas = tareaRepo.countBySprintId(sprintIdStr);
        long tareasCompletadas = tareaRepo.countCompletedBySprintId(sprintIdStr);
        long tareasEnProgreso = tareaRepo.countInProgressBySprintId(sprintIdStr);
        long tareasPendientes = totalTareas - tareasCompletadas - tareasEnProgreso;

        dto.setTotalTareas(totalTareas);
        dto.setTareasCompletadas(tareasCompletadas);
        dto.setTareasEnProgreso(tareasEnProgreso);
        dto.setTareasPendientes(Math.max(0, tareasPendientes));

        double porcentaje = totalTareas == 0 ? 0 : (double) tareasCompletadas / totalTareas * 100;
        dto.setPorcentajeCompletado(redondear(porcentaje));

        // Métricas de horas
        Double horasEstimadas = tareaRepo.sumEstimatedHoursBySprintId(sprintIdStr);
        Double horasReales = tareaRepo.sumRealHoursBySprintId(sprintIdStr);
        horasEstimadas = horasEstimadas != null ? horasEstimadas : 0.0;
        horasReales = horasReales != null ? horasReales : 0.0;

        dto.setHorasEstimadas(redondear(horasEstimadas));
        dto.setHorasReales(redondear(horasReales));

        double eficiencia = horasReales == 0 ? 100 : (horasEstimadas / horasReales) * 100;
        dto.setEficienciaHoras(redondear(eficiencia));

        // Tareas por día (velocidad)
        if (dto.getDiasTotales() > 0 && tareasCompletadas > 0) {
            long diasTranscurridos = dto.getDiasTotales() - dto.getDiasRestantes();
            if (diasTranscurridos > 0) {
                dto.setTareasCompletadasPorDia(redondear((double) tareasCompletadas / diasTranscurridos));
            }
        }

        // Horas promedio por tarea
        if (tareasCompletadas > 0 && horasReales > 0) {
            dto.setHorasPromedioTarea(redondear(horasReales / tareasCompletadas));
        }

        // Cumplimiento del sprint (basado en porcentaje completado)
        dto.setCumplimientoSprint(redondear(porcentaje));

        return dto;
    }

    @Override
    public List<BurndownPointDto> getBurndownSprint(Long sprintId) {
        Sprint sprint = sprintRepo.findById(sprintId).orElse(null);
        if (sprint == null || sprint.getFechaInicio() == null || sprint.getDuracion() == null) {
            return Collections.emptyList();
        }

        String sprintIdStr = String.valueOf(sprintId);
        List<BurndownPointDto> puntos = new ArrayList<>();

        LocalDate fechaInicio = sprint.getFechaInicio();
        LocalDate fechaFin = sprint.getDuracion();
        LocalDate hoy = LocalDate.now();

        // Total de tareas y horas al inicio
        long totalTareas = tareaRepo.countBySprintId(sprintIdStr);
        Double totalHoras = tareaRepo.sumEstimatedHoursBySprintId(sprintIdStr);
        totalHoras = totalHoras != null ? totalHoras : 0.0;

        // Obtener tareas completadas ordenadas por fecha
        List<Tarea> tareasCompletadas = tareaRepo.findCompletedTasksBySprintIdOrderByCompletedAt(sprintIdStr);

        // Crear mapa de tareas completadas por día
        Map<LocalDate, Long> completadasPorDia = new HashMap<>();
        Map<LocalDate, Double> horasCompletadasPorDia = new HashMap<>();

        for (Tarea t : tareasCompletadas) {
            if (t.getCompletedAt() != null) {
                LocalDate fechaCompletado = t.getCompletedAt().toLocalDate();
                completadasPorDia.merge(fechaCompletado, 1L, Long::sum);
                Double horas = t.getRealHours() != null ? t.getRealHours() :
                              (t.getEstimatedHours() != null ? t.getEstimatedHours() : 0.0);
                horasCompletadasPorDia.merge(fechaCompletado, horas, Double::sum);
            }
        }

        // Calcular días totales para la línea ideal
        long diasTotales = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        if (diasTotales <= 0) diasTotales = 1;

        double decrementoDiarioTareas = (double) totalTareas / diasTotales;
        double decrementoDiarioHoras = totalHoras / diasTotales;

        // Generar puntos para cada día del sprint
        long tareasRestantes = totalTareas;
        double horasRestantes = totalHoras;
        long acumuladoCompletadas = 0;
        double acumuladoHoras = 0;

        LocalDate fechaLimite = hoy.isBefore(fechaFin) ? hoy : fechaFin;

        for (LocalDate fecha = fechaInicio; !fecha.isAfter(fechaLimite); fecha = fecha.plusDays(1)) {
            // Calcular completadas ese día
            long completadasHoy = completadasPorDia.getOrDefault(fecha, 0L);
            double horasHoy = horasCompletadasPorDia.getOrDefault(fecha, 0.0);

            acumuladoCompletadas += completadasHoy;
            acumuladoHoras += horasHoy;

            tareasRestantes = totalTareas - acumuladoCompletadas;
            horasRestantes = totalHoras - acumuladoHoras;

            // Calcular ideal para este día
            long diasDesdeInicio = ChronoUnit.DAYS.between(fechaInicio, fecha);
            double idealRestante = totalTareas - (decrementoDiarioTareas * diasDesdeInicio);

            BurndownPointDto punto = new BurndownPointDto(
                fecha,
                Math.max(0, tareasRestantes),
                acumuladoCompletadas,
                Math.max(0, horasRestantes),
                acumuladoHoras,
                Math.max(0, redondear(idealRestante))
            );

            puntos.add(punto);
        }

        return puntos;
    }

    @Override
    public List<VelocidadSprintDto> getVelocidadProyecto(Long proyectoId) {
        List<Sprint> sprints = sprintRepo.findByProjectIdOrderByNumeroAsc(proyectoId);

        return sprints.stream().map(sprint -> {
            VelocidadSprintDto dto = new VelocidadSprintDto();
            dto.setSprintId(sprint.getId());
            dto.setNumeroSprint(sprint.getNumero());
            dto.setTituloSprint(sprint.getTituloSprint());

            String sprintIdStr = String.valueOf(sprint.getId());

            // Tareas planeadas vs completadas
            long totalTareas = tareaRepo.countBySprintId(sprintIdStr);
            long tareasCompletadas = tareaRepo.countCompletedBySprintId(sprintIdStr);

            dto.setTareasPlaneadas(totalTareas);
            dto.setTareasCompletadas(tareasCompletadas);

            // Horas planeadas vs completadas
            Double horasPlaneadas = tareaRepo.sumEstimatedHoursBySprintId(sprintIdStr);
            Double horasCompletadas = tareaRepo.sumRealHoursCompletedBySprintId(sprintIdStr);

            dto.setHorasPlaneadas(horasPlaneadas != null ? redondear(horasPlaneadas) : 0);
            dto.setHorasCompletadas(horasCompletadas != null ? redondear(horasCompletadas) : 0);

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<KpiMiembroDto> getKpisMiembrosProyecto(Long proyectoId) {
        // Obtener todos los assigneeIds únicos del proyecto
        List<String> assigneeIds = tareaRepo.findDistinctAssigneeIdsByProjectId(proyectoId);

        return assigneeIds.stream()
                .filter(Objects::nonNull)
                .map(assigneeId -> calcularKpiMiembro(assigneeId, proyectoId))
                .collect(Collectors.toList());
    }

    @Override
    public KpiMiembroDto getKpisMiembro(String odaId, Long proyectoId) {
        return calcularKpiMiembro(odaId, proyectoId);
    }

    private KpiMiembroDto calcularKpiMiembro(String assigneeId, Long proyectoId) {
        KpiMiembroDto dto = new KpiMiembroDto();
        dto.setOdaId(assigneeId);

        // Intentar obtener nombre del assignee de las tareas
        List<Tarea> tareas = tareaRepo.findByAssigneeIdAndProjectIdOrderByCreatedAtDesc(assigneeId, proyectoId);
        if (!tareas.isEmpty()) {
            dto.setNombre(tareas.get(0).getAssigneeName());
            dto.setEmail(tareas.get(0).getUserEmail());
        }

        // Métricas de tareas
        long totalAsignadas = tareaRepo.countByAssigneeIdAndProjectId(assigneeId, proyectoId);
        long completadas = tareaRepo.countCompletedByAssigneeIdAndProjectId(assigneeId, proyectoId);
        long pendientes = totalAsignadas - completadas;

        dto.setTareasAsignadas(totalAsignadas);
        dto.setTareasCompletadas(completadas);
        dto.setTareasPendientes(Math.max(0, pendientes));

        double porcentaje = totalAsignadas == 0 ? 0 : (double) completadas / totalAsignadas * 100;
        dto.setPorcentajeCompletado(redondear(porcentaje));

        // Métricas de horas (globales del usuario, no solo del proyecto)
        Double horasEstimadas = tareaRepo.sumEstimatedHoursByAssigneeId(assigneeId);
        Double horasReales = tareaRepo.sumRealHoursByAssigneeId(assigneeId);
        horasEstimadas = horasEstimadas != null ? horasEstimadas : 0.0;
        horasReales = horasReales != null ? horasReales : 0.0;

        dto.setHorasEstimadas(redondear(horasEstimadas));
        dto.setHorasReales(redondear(horasReales));

        double eficiencia = horasReales == 0 ? 100 : (horasEstimadas / horasReales) * 100;
        dto.setEficiencia(redondear(eficiencia));

        // Horas promedio por tarea
        if (completadas > 0 && horasReales > 0) {
            dto.setHorasPromedioPorTarea(redondear(horasReales / completadas));
        }

        return dto;
    }

    @Override
    public List<KpiProyectoDto> getKpisProyectosUsuario(Long usuarioId) {
        // Obtener proyectos visibles para el usuario
        List<Proyecto> proyectos = getProyectosVisibles(usuarioId);

        return proyectos.stream()
                .map(p -> getKpisProyecto(p.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Proyecto> getProyectosVisibles(Long usuarioId) {
        Map<Long, Proyecto> byId = new LinkedHashMap<>();

        // Proyectos creados por el usuario
        for (Proyecto p : proyectoRepo.findByCreadorId(usuarioId)) {
            if (p != null && p.getId() != null) {
                byId.putIfAbsent(p.getId(), p);
            }
        }

        // Proyectos de equipos donde es miembro
        List<UsuarioEquipo> rels = usuarioEquipoRepo.findByUsuarioId(usuarioId);
        Set<Long> equipoIds = rels.stream()
                .filter(Objects::nonNull)
                .map(ue -> ue.getId() != null ? ue.getId().getEquipoId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (Long equipoId : equipoIds) {
            for (Proyecto p : proyectoRepo.findByEquipoId(equipoId)) {
                if (p != null && p.getId() != null) {
                    byId.putIfAbsent(p.getId(), p);
                }
            }
        }

        return new ArrayList<>(byId.values());
    }

    @Override
    public DashboardDto getDashboardProyecto(Long proyectoId) {
        Proyecto proyecto = proyectoRepo.findById(proyectoId).orElse(null);
        if (proyecto == null) {
            return null;
        }

        DashboardDto dashboard = new DashboardDto();
        dashboard.setProyectoId(proyectoId);
        dashboard.setNombreProyecto(proyecto.getNombreProyecto());
        dashboard.setEstado(proyecto.getEstado());

        // KPIs del proyecto
        dashboard.setKpisProyecto(getKpisProyecto(proyectoId));

        // Sprint activo
        LocalDate hoy = LocalDate.now();
        List<Sprint> sprintsActivos = sprintRepo.findActiveByProjectId(proyectoId, hoy);
        if (!sprintsActivos.isEmpty()) {
            Sprint sprintActivo = sprintsActivos.get(0);
            dashboard.setSprintActivo(getKpisSprint(sprintActivo.getId()));

            // Burndown del sprint activo
            dashboard.setBurndown(getBurndownSprint(sprintActivo.getId()));
        }

        // Velocidad histórica
        dashboard.setVelocidad(getVelocidadProyecto(proyectoId));

        // KPIs de miembros
        dashboard.setMiembros(getKpisMiembrosProyecto(proyectoId));

        // Todos los sprints
        dashboard.setSprints(getKpisSprintsPorProyecto(proyectoId));

        return dashboard;
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
