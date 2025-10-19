package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.dto.CompleteTaskDto;
import com.springboot.MyTodoList.dto.TaskCreateDto;
import com.springboot.MyTodoList.dto.UpdateStatusDto;
import com.springboot.MyTodoList.dto.UpdateTaskDto;
import com.springboot.MyTodoList.model.Proyecto;
import com.springboot.MyTodoList.model.UsuarioEquipoId;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.ProyectoRepository;
import com.springboot.MyTodoList.repository.TareaRepository;
import com.springboot.MyTodoList.repository.UsuarioEquipoRepository;
import com.springboot.MyTodoList.service.TareaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
@RestController
@RequestMapping("/api")
public class TareaController {

    private final TareaService tareaService;
    private final TareaRepository tareaRepository;
    private final ProyectoRepository proyectoRepository;           // ✅ nuevo
    private final UsuarioEquipoRepository usuarioEquipoRepository; // ✅ nuevo

    public TareaController(TareaService tareaService,
                           TareaRepository tareaRepository,
                           ProyectoRepository proyectoRepository,
                           UsuarioEquipoRepository usuarioEquipoRepository) {
        this.tareaService = tareaService;
        this.tareaRepository = tareaRepository;
        this.proyectoRepository = proyectoRepository;
        this.usuarioEquipoRepository = usuarioEquipoRepository;
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

    // Helper: leer X-User-Id (opcional). Si no llega, devolvemos null.
    private Long readUserId(HttpServletRequest req) {
        String idH = req.getHeader("X-User-Id");
        if (idH == null || idH.isBlank()) return null;
        try { return Long.parseLong(idH.trim()); } catch (NumberFormatException e) { return null; }
    }

    // Helper: checar membresía del equipo del proyecto
    private boolean isMemberOfProjectTeam(Long userId, Long projectId) {
        if (userId == null || projectId == null) return true; // modo dev: permitir si no hay userId
        Optional<Proyecto> opt = proyectoRepository.findById(projectId);
        if (opt.isEmpty()) return false;
        Long equipoId = opt.get().getEquipoId();
        if (equipoId == null) return false;
        return usuarioEquipoRepository.existsById(new UsuarioEquipoId(userId, equipoId));
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

    // LIST (modo dueño y modo equipo/compartido)
    @GetMapping("/tasks")
    public ResponseEntity<?> list(HttpServletRequest req,
                                  @RequestParam(name = "projectId", required = false) Long projectId,
                                  @RequestParam(name = "sprintId",  required = false) String sprintId) {
        // 1) Si NO viene X-User-Email y SÍ viene projectId -> vista COMPARTIDA del proyecto (todas las tareas)
        String emailHeader = req.getHeader("X-User-Email");
        if ((emailHeader == null || emailHeader.isBlank()) && projectId != null) {
            Long userId = readUserId(req); // opcional; si viene lo usamos para validar membresía
            if (!isMemberOfProjectTeam(userId, projectId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No eres miembro del equipo del proyecto");
            }
            // ✅ devolver todas las tareas del proyecto (mismas para todos)
            List<Tarea> tasks = tareaRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
            return ResponseEntity.ok(tasks);
        }

        // 2) Modo clásico: vista del dueño (requiere X-User-Email)
        String owner = requireUserEmail(req);

        if (projectId != null && sprintId != null && !sprintId.isBlank()) {
            return ResponseEntity.ok(
                tareaRepository.findByAssigneeIdAndProjectIdAndSprintIdOrderByCreatedAtDesc(owner, projectId, sprintId)
            );
        }
        if (projectId != null) {
            return ResponseEntity.ok(
                tareaRepository.findByAssigneeIdAndProjectIdOrderByCreatedAtDesc(owner, projectId)
            );
        }
        if (sprintId != null && !sprintId.isBlank()) {
            return ResponseEntity.ok(
                tareaRepository.findByAssigneeIdAndSprintIdOrderByCreatedAtDesc(owner, sprintId)
            );
        }
        return ResponseEntity.ok(tareaService.listByAssignee(owner));
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

    // === tus endpoints de métricas se mantienen igual ===

    @GetMapping("/tasks/productivity")
    public ResponseEntity<Map<String, Object>> productivity(HttpServletRequest req) {
        String owner = requireUserEmail(req);
        List<Tarea> tasks = tareaRepository.findByAssigneeId(owner);

        long totalAssigned = tasks.size();
        long totalCompleted = tasks.stream()
                .filter(t -> (t.getStatus() != null && t.getStatus().equalsIgnoreCase("Hecho")) || t.getCompletedAt() != null)
                .count();

        double plannedHours = tasks.stream().mapToDouble(t -> t.getEstimatedHours() != null ? t.getEstimatedHours() : 0.0).sum();
        double realHours    = tasks.stream().mapToDouble(t -> t.getRealHours() != null ? t.getRealHours() : 0.0).sum();

        double totalProgressSum = tasks.stream().mapToDouble(t -> {
            boolean done = (t.getStatus() != null && t.getStatus().equalsIgnoreCase("Hecho")) || t.getCompletedAt() != null;
            if (done) return 1.0;
            Double est = t.getEstimatedHours();
            Double real = t.getRealHours();
            if (est != null && est > 0.0) return Math.min((real != null ? real : 0.0)/est, 1.0);
            return (real != null && real > 0.0) ? 0.5 : 0.0;
        }).sum();

        double avgProgress = totalAssigned == 0 ? 0.0 : (totalProgressSum / totalAssigned);
        double hoursRatio =
                (plannedHours == 0 && realHours == 0) ? 1.0 :
                (plannedHours == 0) ? 0.0 :
                (realHours == 0) ? 1.0 : plannedHours / realHours;

        double productivity = Math.max(0.0, Math.min(avgProgress * hoursRatio * 100.0, 100.0));

        Map<String, Object> resp = new HashMap<>();
        resp.put("assignee", owner);
        resp.put("totalAssigned", totalAssigned);
        resp.put("totalCompleted", totalCompleted);
        resp.put("plannedHours", plannedHours);
        resp.put("realHours", realHours);
        resp.put("avgProgress", avgProgress);
        resp.put("tasksRatio", avgProgress);
        resp.put("hoursRatio", hoursRatio);
        resp.put("productivityPercent", productivity);

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/tasks/avg-resolution/all")
    public Map<String, Object> avgResolutionAll() {
        Double avg = tareaRepository.avgRealHoursOfCompletedTasks();
        Map<String, Object> resp = new HashMap<>();
        resp.put("avgResolutionHours", avg == null ? 0.0 : avg);
        return resp;
    }

    @GetMapping("/tasks/avg-resolution/all-tasks")
    public Map<String, Object> avgResolutionAllTasks() {
        Double avg = tareaRepository.avgRealHoursAllTasks();
        Map<String, Object> resp = new HashMap<>();
        resp.put("avgResolutionHoursAllTasks", avg == null ? 0.0 : avg);
        return resp;
    }
}
