package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.dto.CompleteTaskDto;
import com.springboot.MyTodoList.dto.TaskCreateDto;
import com.springboot.MyTodoList.dto.UpdateStatusDto;
import com.springboot.MyTodoList.dto.UpdateTaskDto;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.TareaRepository;
import com.springboot.MyTodoList.service.TareaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Objects;

@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
@RestController
@RequestMapping("/api")
public class TareaController {

    private final TareaService tareaService;
    private final TareaRepository tareaRepository;

    public TareaController(TareaService tareaService, TareaRepository tareaRepository) {
        this.tareaService = tareaService;
        this.tareaRepository = tareaRepository;
    }

    private String requireUserEmail(HttpServletRequest req) {
        String email = req.getHeader("X-User-Email");
        if (email == null || email.trim().isEmpty()) {
            email = req.getParameter("email");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Missing owner: provide X-User-Email header OR ?email=");
        }
        return email.trim();
    }

    // CREATE (projectId obligatorio)
    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(HttpServletRequest req, @Valid @RequestBody TaskCreateDto dto) {
        if (dto.projectId == null || dto.projectId < 1) {
            return ResponseEntity.badRequest().body("El campo projectId es obligatorio y debe ser > 0.");
        }
        if (dto.estimatedHours != null && dto.estimatedHours > 4.0) {
            return ResponseEntity.badRequest().body("Máximo 4 horas por tarea. Divide en subtareas.");
        }
        String owner = requireUserEmail(req);
        dto.assigneeId = owner; // forzar dueño
        Tarea created = tareaService.createFromDto(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // LIST (filtros opcionales)
    @GetMapping("/tasks")
    public List<Tarea> list(HttpServletRequest req,
                            @RequestParam(name = "projectId", required = false) Long projectId,
                            @RequestParam(name = "sprintId",  required = false) String sprintId) {
        String owner = requireUserEmail(req);

        if (projectId != null && sprintId != null && !sprintId.isBlank()) {
            return tareaRepository.findByAssigneeIdAndProjectIdAndSprintIdOrderByCreatedAtDesc(owner, projectId, sprintId);
        }
        if (projectId != null) {
            return tareaRepository.findByAssigneeIdAndProjectIdOrderByCreatedAtDesc(owner, projectId);
        }
        if (sprintId != null && !sprintId.isBlank()) {
            return tareaRepository.findByAssigneeIdAndSprintIdOrderByCreatedAtDesc(owner, sprintId);
        }
        return tareaService.listByAssignee(owner);
    }

    @PatchMapping("/tasks/{id}/complete")
    public Tarea complete(HttpServletRequest req, @PathVariable Long id, @RequestBody CompleteTaskDto dto) {
        String owner = requireUserEmail(req);
        tareaRepository.findByIdAndAssigneeId(id, owner)
                .orElseThrow(() -> new NoSuchElementException("Task not found or not owned by user"));
        return tareaService.completeTask(id, dto);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> delete(HttpServletRequest req, @PathVariable Long id) {
        String owner = requireUserEmail(req);
        tareaRepository.findByIdAndAssigneeId(id, owner)
                .orElseThrow(() -> new NoSuchElementException("Task not found or not owned by user"));
        tareaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/tasks/{id}")
    public Tarea update(HttpServletRequest req, @PathVariable Long id, @RequestBody UpdateTaskDto dto) {
        String owner = requireUserEmail(req);
        tareaRepository.findByIdAndAssigneeId(id, owner)
                .orElseThrow(() -> new NoSuchElementException("Task not found or not owned by user"));

        if (dto != null && dto.assigneeId != null && !Objects.equals(dto.assigneeId, owner)) {
            dto.assigneeId = owner;
        }
        if (dto != null && dto.projectId != null && dto.projectId < 1) {
            throw new IllegalArgumentException("projectId debe ser > 0");
        }
        return tareaService.updateTask(id, dto);
    }

    @PatchMapping("/tasks/{id}/status")
    public Tarea updateStatus(HttpServletRequest req, @PathVariable Long id, @RequestBody UpdateStatusDto body) {
        String owner = requireUserEmail(req);
        tareaRepository.findByIdAndAssigneeId(id, owner)
                .orElseThrow(() -> new NoSuchElementException("Task not found or not owned by user"));
        return tareaService.updateStatus(id, body.status);
    }

    // Nuevo endpoint: productividad del usuario
    @GetMapping("/tasks/productivity")
    public ResponseEntity<Map<String, Object>> productivity(HttpServletRequest req) {
        String owner = requireUserEmail(req);
        List<Tarea> tasks = tareaRepository.findByAssigneeId(owner);

        long totalAssigned = tasks.size();

        // Conservamos totalCompleted para compatibilidad, pero la métrica principal será avgProgress
        long totalCompleted = tasks.stream()
                .filter(t -> {
                    if (t.getStatus() != null && t.getStatus().equalsIgnoreCase("Hecho")) return true;
                    return t.getCompletedAt() != null;
                }).count();

        double plannedHours = tasks.stream()
                .mapToDouble(t -> t.getEstimatedHours() != null ? t.getEstimatedHours() : 0.0)
                .sum();
        double realHours = tasks.stream()
                .mapToDouble(t -> t.getRealHours() != null ? t.getRealHours() : 0.0)
                .sum();

        // Nuevo: calcular avance real por tarea y luego el promedio (avgProgress)
        double totalProgressSum = tasks.stream().mapToDouble(t -> {
            boolean done = (t.getStatus() != null && t.getStatus().equalsIgnoreCase("Hecho")) || t.getCompletedAt() != null;
            if (done) {
                return 1.0;
            }
            Double est = t.getEstimatedHours();
            Double real = t.getRealHours();
            if (est != null && est > 0.0) {
                if (real != null && real > 0.0) {
                    return Math.min(real / est, 1.0);
                } else {
                    return 0.0;
                }
            } else {
                // Heurística: si no hay estimado pero hay horas reales, consideramos progreso parcial (50%)
                if (real != null && real > 0.0) {
                    return 0.5;
                } else {
                    return 0.0;
                }
            }
        }).sum();

        double avgProgress = totalAssigned == 0 ? 0.0 : (totalProgressSum / totalAssigned); // 0..1

        // Mantener la componente de horas como antes
        double hoursRatio;
        if (plannedHours == 0 && realHours == 0) {
            hoursRatio = 1.0;
        } else if (plannedHours == 0) {
            hoursRatio = 0.0;
        } else if (realHours == 0) {
            hoursRatio = 1.0;
        } else {
            hoursRatio = plannedHours / realHours;
        }

        // Ahora la productividad usa avgProgress (avance real del usuario) en lugar de completed/assigned
        double productivity = avgProgress * hoursRatio * 100.0;
        if (productivity < 0) productivity = 0;
        if (productivity > 100.0) productivity = 100.0;

        Map<String, Object> resp = new HashMap<>();
        resp.put("assignee", owner);
        resp.put("totalAssigned", totalAssigned);
        resp.put("totalCompleted", totalCompleted);
        resp.put("plannedHours", plannedHours);
        resp.put("realHours", realHours);
        resp.put("avgProgress", avgProgress);           // nuevo: promedio de avance por tarea (0..1)
        resp.put("tasksRatio", avgProgress);           // compatibilidad: tasksRatio refleja ahora avgProgress
        resp.put("hoursRatio", hoursRatio);
        resp.put("productivityPercent", productivity);

        return ResponseEntity.ok(resp);
    }
}
