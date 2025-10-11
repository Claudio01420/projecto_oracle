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
}
