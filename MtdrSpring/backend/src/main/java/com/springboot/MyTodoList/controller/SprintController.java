package com.springboot.MyTodoList.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.dto.SprintCreateDto;
import com.springboot.MyTodoList.dto.SprintUpdateDto;
import com.springboot.MyTodoList.model.Proyecto;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.ProyectoRepository;
import com.springboot.MyTodoList.repository.SprintRepository;
import com.springboot.MyTodoList.repository.TareaRepository;
import com.springboot.MyTodoList.service.NotificacionService;

@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
@RestController
@RequestMapping("/sprints")
public class SprintController {

    private final SprintRepository sprintRepository;
    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;
    private final NotificacionService notificacionService;

    public SprintController(
            SprintRepository sprintRepository,
            ProyectoRepository proyectoRepository,
            TareaRepository tareaRepository,
            NotificacionService notificacionService
    ) {
        this.sprintRepository = sprintRepository;
        this.proyectoRepository = proyectoRepository;
        this.tareaRepository = tareaRepository;
        this.notificacionService = notificacionService;
    }

    // ==================== CRUD BÁSICO DE SPRINT ====================

    @GetMapping
    public List<Sprint> all(@RequestParam(value = "projectId", required = false) Long projectId) {
        if (projectId != null) {
            return sprintRepository.findByProjectIdOrderByNumeroAsc(projectId);
        }
        return sprintRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sprint> one(@PathVariable Long id) {
        return sprintRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody SprintCreateDto dto,
            @RequestHeader(value = "X-User-Id", required = false) Long userIdHeader
    ) {
        if (dto.projectId == null) {
            return ResponseEntity.badRequest()
                    .body("El campo projectId es obligatorio para crear un sprint.");
        }
        if (dto.tituloSprint == null || dto.tituloSprint.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("El título del sprint es obligatorio.");
        }

        Proyecto p = proyectoRepository.findById(dto.projectId).orElse(null);
        if (p == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No existe el proyecto con id=" + dto.projectId);
        }

        Integer numero = dto.numero;
        if (numero == null) {
            List<Sprint> existentes = sprintRepository.findByProjectId(dto.projectId);
            numero = existentes.size() + 1;
        }

        LocalDate fechaInicio = dto.fechaInicio != null
                ? dto.fechaInicio
                : LocalDate.now();

        LocalDate fechaFin = dto.duracion;
        if (fechaFin == null) {
            fechaFin = fechaInicio.plusDays(7);
        }

        Sprint sprint = new Sprint();
        sprint.setProjectId(dto.projectId);
        sprint.setTituloSprint(dto.tituloSprint);
        sprint.setDescripcionSprint(dto.descripcionSprint);
        sprint.setNumero(numero);
        sprint.setFechaInicio(fechaInicio);
        sprint.setDuracion(fechaFin);

        Sprint saved = sprintRepository.save(sprint);

        try {
            notificacionService.notificarSprintCreado(saved, p, userIdHeader);
        } catch (Exception ignored) {}

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody SprintUpdateDto body,
            @RequestHeader(value = "X-User-Id", required = false) Long userIdHeader
    ) {
        return sprintRepository.findById(id)
                .map(actual -> {

                    if (body.getTituloSprint() != null) {
                        actual.setTituloSprint(body.getTituloSprint());
                    }
                    if (body.getDescripcionSprint() != null) {
                        actual.setDescripcionSprint(body.getDescripcionSprint());
                    }
                    if (body.getDuracion() != null) {        // fecha fin
                        actual.setDuracion(body.getDuracion());
                    }
                    if (body.getNumero() != null) {
                        actual.setNumero(body.getNumero());
                    }
                    if (body.getProjectId() != null) {
                        actual.setProjectId(body.getProjectId());
                    }
                    if (body.getFechaInicio() != null) {
                        actual.setFechaInicio(body.getFechaInicio());
                    }

                    Sprint saved = sprintRepository.save(actual);

                    Proyecto p = (saved.getProjectId() != null)
                            ? proyectoRepository.findById(saved.getProjectId()).orElse(null)
                            : null;
                    try {
                        notificacionService.notificarSprintActualizado(saved, p, userIdHeader);
                    } catch (Exception ignored) {}

                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userIdHeader
    ) {
        return sprintRepository.findById(id).map(s -> {
            Proyecto p = (s.getProjectId() != null)
                    ? proyectoRepository.findById(s.getProjectId()).orElse(null)
                    : null;
            try {
                notificacionService.notificarSprintEliminado(s, p, userIdHeader);
            } catch (Exception ignored) {}

            sprintRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ==================== RELACIÓN SPRINT <-> TAREAS ====================

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<Tarea>> tasksOfSprint(@PathVariable Long id) {
        String sprintKey = String.valueOf(id);
        List<Tarea> tasks = tareaRepository.findAll().stream()
                .filter(t -> sprintKey.equals(t.getSprintId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<?> attachTaskToSprint(@PathVariable Long id, @PathVariable Long taskId) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Sprint no encontrado: " + id));

        Tarea tarea = tareaRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Tarea no encontrada: " + taskId));

        if (sprint.getProjectId() != null &&
            tarea.getProjectId() != null &&
            !sprint.getProjectId().equals(tarea.getProjectId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("La tarea no pertenece al mismo proyecto del sprint.");
        }

        tarea.setSprintId(String.valueOf(id));
        Tarea saved = tareaRepository.save(tarea);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<?> detachTaskFromSprint(@PathVariable Long id, @PathVariable Long taskId) {
        Tarea tarea = tareaRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Tarea no encontrada: " + taskId));

        String sprintKey = String.valueOf(id);
        if (!sprintKey.equals(tarea.getSprintId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("La tarea no pertenece a este sprint.");
        }

        tarea.setSprintId(null);
        Tarea saved = tareaRepository.save(tarea);
        return ResponseEntity.ok(saved);
    }
}
