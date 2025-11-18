package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.dto.SprintCreateDto;
import com.springboot.MyTodoList.dto.SprintUpdateDto;
import com.springboot.MyTodoList.model.Proyecto;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.ProyectoRepository;
import com.springboot.MyTodoList.repository.SprintRepository;
import com.springboot.MyTodoList.repository.TareaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
@RestController
@RequestMapping("/sprints")
public class SprintController {

    private final SprintRepository sprintRepository;
    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;

    public SprintController(
            SprintRepository sprintRepository,
            ProyectoRepository proyectoRepository,
            TareaRepository tareaRepository
    ) {
        this.sprintRepository = sprintRepository;
        this.proyectoRepository = proyectoRepository;
        this.tareaRepository = tareaRepository;
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

    /**
     * POST /sprints
     * Body: SprintCreateDto
     *
     * JSON esperado:
     * - projectId       (Long, obligatorio)
     * - tituloSprint    (String, obligatorio)
     * - descripcionSprint (String, opcional)
     * - duracion        (LocalDate, opcional) -> FECHA FIN
     * - numero          (Integer, opcional, si null se calcula)
     * - fechaInicio     (LocalDate, opcional, si null se usa hoy)
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody SprintCreateDto dto) {
        if (dto.projectId == null) {
            return ResponseEntity.badRequest()
                    .body("El campo projectId es obligatorio para crear un sprint.");
        }
        if (dto.tituloSprint == null || dto.tituloSprint.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("El título del sprint es obligatorio.");
        }

        // Validar proyecto
        Proyecto p = proyectoRepository.findById(dto.projectId).orElse(null);
        if (p == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No existe el proyecto con id=" + dto.projectId);
        }

        // Número correlativo si viene null
        Integer numero = dto.numero;
        if (numero == null) {
            List<Sprint> existentes = sprintRepository.findByProjectId(dto.projectId);
            numero = existentes.size() + 1;
        }

        // Fecha de inicio
        LocalDate fechaInicio = dto.fechaInicio != null
                ? dto.fechaInicio
                : LocalDate.now();

        // Fecha fin (columna DURACION)
        LocalDate fechaFin = dto.duracion;
        if (fechaFin == null) {
            // Por defecto +7 días
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
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * PUT /sprints/{id}
     * Body: SprintUpdateDto (actualización parcial)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SprintUpdateDto body) {
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
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!sprintRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        sprintRepository.deleteById(id);
        return ResponseEntity.noContent().build();
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
