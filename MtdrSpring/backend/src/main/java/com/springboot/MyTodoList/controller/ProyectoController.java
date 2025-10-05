package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Proyecto;
import com.springboot.MyTodoList.repository.ProyectoRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/proyectos")
@CrossOrigin(origins = "*") // 🔹 NUEVO: permite peticiones desde el HTML
public class ProyectoController {

    private final ProyectoRepository repo;

    public ProyectoController(ProyectoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Proyecto> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Proyecto one(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @PostMapping
    public Proyecto create(@RequestBody Proyecto p) {
        return repo.save(p);
    }

    @DeleteMapping("/{id}") // 🔹 NUEVO: borrar proyectos
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
