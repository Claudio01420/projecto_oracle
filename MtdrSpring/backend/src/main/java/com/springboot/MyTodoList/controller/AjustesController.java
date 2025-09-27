package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Ajustes;
import com.springboot.MyTodoList.repository.AjustesRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ajustes")
public class AjustesController {

    private final AjustesRepository repo;

    public AjustesController(AjustesRepository repo) {
        this.repo = repo;
    }

    
    @GetMapping
    public List<Ajustes> all() {
        return repo.findAll();
    }

    
    @GetMapping("/{id}")
    public Ajustes one(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    
    @PostMapping
    public Ajustes create(@RequestBody Ajustes a) {
        return repo.save(a);
    }
}
