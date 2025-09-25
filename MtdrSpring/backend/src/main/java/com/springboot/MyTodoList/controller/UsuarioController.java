package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Usuario;
import com.springboot.MyTodoList.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository repo;
    public UsuarioController(UsuarioRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Usuario> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Usuario one(@PathVariable Long id) { return repo.findById(id).orElse(null); }

    @PostMapping
    public Usuario create(@RequestBody Usuario u) { return repo.save(u); }
}
