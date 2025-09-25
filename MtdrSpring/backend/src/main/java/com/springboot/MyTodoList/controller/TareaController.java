package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.TareaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tareas")
public class TareaController {

    private final TareaRepository tareaRepository;

    // Constructor sin Lombok
    public TareaController(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    // GET http://localhost:8080/tareas
    @GetMapping
    public List<Tarea> getAll() {
        return tareaRepository.findAll();
    }

    // GET http://localhost:8080/tareas/{id}
    @GetMapping("/{id}")
    public Tarea getOne(@PathVariable Long id) {
        return tareaRepository.findById(id).orElse(null);
    }

    // POST http://localhost:8080/tareas
    @PostMapping
    public Tarea create(@RequestBody Tarea tarea) {
        return tareaRepository.save(tarea);
    }
}

