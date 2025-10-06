package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.dto.CompleteTaskDto;
import com.springboot.MyTodoList.dto.TaskCreateDto;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.service.TareaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TareaController {

    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskCreateDto dto) {
        if (dto.estimatedHours != null && dto.estimatedHours > 4.0) {
            return ResponseEntity.badRequest().body("Máximo 4 horas por tarea. Divide en subtareas.");
        }
        Tarea created = tareaService.createFromDto(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/tasks")
    public List<Tarea> listByAssignee(@RequestParam String assigneeId) {
        return tareaService.listByAssignee(assigneeId);
    }

    @PatchMapping("/tasks/{id}/complete")
    public Tarea complete(@PathVariable Long id, @Valid @RequestBody CompleteTaskDto dto) {
        return tareaService.completeTask(id, dto);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tareaService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}

