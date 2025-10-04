package com.springboot.MyTodoList.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.springboot.MyTodoList.dto.TaskCreateDto;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.service.TareaService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TareaController {

  private final TareaService tareaService;

  public TareaController(TareaService tareaService) {
    this.tareaService = tareaService;
  }

  @PostMapping(value = "/tasks", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> createTask(@Valid @RequestBody TaskCreateDto dto) {
    if (dto.estimatedHours != null && dto.estimatedHours > 4.0) {
      return ResponseEntity.badRequest().body("Máximo 4 horas por tarea. Divide en subtareas.");
    }
    Tarea created = tareaService.createFromDto(dto);
    return new ResponseEntity<>(created, HttpStatus.CREATED);
  }

  @GetMapping(value = "/tasks", produces = "application/json")
  public ResponseEntity<List<Tarea>> listTasks() {
    return ResponseEntity.ok(tareaService.findAll());
  }
}
