package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Equipo;
import com.springboot.MyTodoList.repository.EquipoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipos")
public class EquipoController {

    private final EquipoRepository repo;
    public EquipoController(EquipoRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Equipo> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Equipo one(@PathVariable Long id) { return repo.findById(id).orElse(null); }

    @PostMapping
    public Equipo create(@RequestBody Equipo e) { return repo.save(e); }
}
