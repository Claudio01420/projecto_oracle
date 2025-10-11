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

    /** Obtiene el email del usuario desde header X-User-Email o query ?email= */
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

    // CREATE
    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(HttpServletRequest req, @Valid @RequestBody TaskCreateDto dto) {
        if (dto.estimatedHours != null && dto.estimatedHours > 4.0) {
            return ResponseEntity.badRequest().body("Máximo 4 horas por tarea. Divide en subtareas.");
        }
        String owner = requireUserEmail(req);
        dto.assigneeId = owner; // forzar dueño por email
        Tarea created = tareaService.createFromDto(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // LIST (solo mis tareas)
    @GetMapping("/tasks")
    public List<Tarea> list(HttpServletRequest req) {
        String owner = requireUserEmail(req);
        return tareaService.listByAssignee(owner);
    }

    // COMPLETE
    @PatchMapping("/tasks/{id}/complete")
    public Tarea complete(HttpServletRequest req, @PathVariable Long id, @RequestBody CompleteTaskDto dto) {
        String owner = requireUserEmail(req);
        tareaRepository.findByIdAndAssigneeId(id, owner)
                .orElseThrow(() -> new NoSuchElementException("Task not found or not owned by user"));
        return tareaService.completeTask(id, dto);
    }

    // DELETE
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> delete(HttpServletRequest req, @PathVariable Long id) {
        String owner = requireUserEmail(req);
        tareaRepository.findByIdAndAssigneeId(id, owner)
                .orElseThrow(() -> new NoSuchElementException("Task not found or not owned by user"));
        tareaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // UPDATE (full)
    @PutMapping("/tasks/{id}")
    public Tarea update(HttpServletRequest req, @PathVariable Long id, @RequestBody UpdateTaskDto dto) {
        String owner = requireUserEmail(req);
        tareaRepository.findByIdAndAssigneeId(id, owner)
                .orElseThrow(() -> new NoSuchElementException("Task not found or not owned by user"));
        if (dto != null && dto.assigneeId != null && !Objects.equals(dto.assigneeId, owner)) {
            dto.assigneeId = owner; // no permitir cambiar de dueño
        }
        return tareaService.updateTask(id, dto);
    }

    // UPDATE STATUS (drag&drop)
    @PatchMapping("/tasks/{id}/status")
    public Tarea updateStatus(HttpServletRequest req, @PathVariable Long id, @RequestBody UpdateStatusDto body) {
        String owner = requireUserEmail(req);
        tareaRepository.findByIdAndAssigneeId(id, owner)
                .orElseThrow(() -> new NoSuchElementException("Task not found or not owned by user"));
        return tareaService.updateStatus(id, body.status);
    }
}
