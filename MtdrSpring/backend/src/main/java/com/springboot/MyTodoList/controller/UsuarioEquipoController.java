package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.UsuarioEquipo;
import com.springboot.MyTodoList.model.UsuarioEquipoId;
import com.springboot.MyTodoList.repository.UsuarioEquipoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/usuarios-equipos")
public class UsuarioEquipoController {

    private final UsuarioEquipoRepository repo;

    public UsuarioEquipoController(UsuarioEquipoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<UsuarioEquipo> all() { 
        return repo.findAll(); 
    }

    // NUEVO: listar todas las membresías de un usuario en formato PLANO:
    // [
    //   { "usuarioId": 1, "equipoId": 7, "rol": "ADMIN" },
    //   { "usuarioId": 1, "equipoId": 9, "rol": "USUARIO" }
    // ]
    @GetMapping("/usuario/{usuarioId}")
    public List<Map<String,Object>> byUsuarioFlat(@PathVariable Long usuarioId) {
        return repo.findByUsuarioId(usuarioId).stream()
            .map(ue -> {
                Map<String,Object> m = new HashMap<>();
                Long uid = ue.getId() != null ? ue.getId().getUsuarioId() : null;
                Long eid = ue.getId() != null ? ue.getId().getEquipoId()  : null;
                m.put("usuarioId", uid);
                m.put("equipoId", eid);
                m.put("rol", ue.getRol() != null ? ue.getRol().name() : null);
                return m;
            }).collect(Collectors.toList());
    }

    // GET /usuarios-equipos/usuario/1/equipo/2  (compatibilidad con tu front)
    @GetMapping("/usuario/{usuarioId}/equipo/{equipoId}")
    public UsuarioEquipo one(@PathVariable Long usuarioId, @PathVariable Long equipoId) {
        UsuarioEquipoId id = new UsuarioEquipoId(usuarioId, equipoId);
        return repo.findById(id).orElse(null);
    }

    @PostMapping
    public UsuarioEquipo create(@RequestBody UsuarioEquipo ue) {
        // Body: {"id":{"usuarioId":1,"equipoId":2},"rol":"ADMIN"}
        return repo.save(ue);
    }
}
