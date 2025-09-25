package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Notificacion;
import com.springboot.MyTodoList.repository.NotificacionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionRepository repo;
    public NotificacionController(NotificacionRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Notificacion> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Notificacion one(@PathVariable Long id) { return repo.findById(id).orElse(null); }

    @PostMapping
    public Notificacion create(@RequestBody Notificacion n) { return repo.save(n); }
}
