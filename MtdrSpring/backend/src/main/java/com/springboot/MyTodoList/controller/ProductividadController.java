package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Productividad;
import com.springboot.MyTodoList.repository.ProductividadRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productividad")
public class ProductividadController {

    private final ProductividadRepository repo;
    public ProductividadController(ProductividadRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Productividad> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Productividad one(@PathVariable Long id) { return repo.findById(id).orElse(null); }

    @PostMapping
    public Productividad create(@RequestBody Productividad p) { return repo.save(p); }
}
