package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.UsuarioEquipo;
import com.springboot.MyTodoList.model.UsuarioEquipoId;
import com.springboot.MyTodoList.repository.UsuarioEquipoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios-equipos")
public class UsuarioEquipoController {

    private final UsuarioEquipoRepository repo;
    public UsuarioEquipoController(UsuarioEquipoRepository repo) { this.repo = repo; }

    @GetMapping
    public List<UsuarioEquipo> all() { return repo.findAll(); }

    // GET /usuarios-equipos/usuario/1/equipo/2
    @GetMapping("/usuario/{usuarioId}/equipo/{equipoId}")
    public UsuarioEquipo one(@PathVariable Long usuarioId, @PathVariable Long equipoId) {
        UsuarioEquipoId id = new UsuarioEquipoId(usuarioId, equipoId);
        return repo.findById(id).orElse(null);
    }

    @PostMapping
    public UsuarioEquipo create(@RequestBody UsuarioEquipo ue) {
        return repo.save(ue); // Body debe contener {"id":{"usuarioId":1,"equipoId":2}}
    }
}
