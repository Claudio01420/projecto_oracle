package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Equipo;
import com.springboot.MyTodoList.repository.EquipoRepository;
import com.springboot.MyTodoList.service.EquipoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/equipos")
public class EquipoController {

    private final EquipoRepository repo;
    private final EquipoService equipoService;

    public EquipoController(EquipoRepository repo, EquipoService equipoService) {
        this.repo = repo;
        this.equipoService = equipoService;
    }

    @GetMapping
    public List<Equipo> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Equipo one(@PathVariable Long id) { return repo.findById(id).orElse(null); }

    @PostMapping
    public Equipo create(@RequestBody Equipo e) { return repo.save(e); }

    // Crear equipo y vincular creador como ADMIN
    @PostMapping("/crear-con-admin")
    public Equipo crearConAdmin(@RequestParam("creadorId") Long creadorId, @RequestBody Equipo e) {
        return equipoService.crearEquipoConAdmin(e, creadorId);
    }

    // (Opcional) Editar nombre/descripcion
     @PutMapping("/{id}")
     public Equipo update(@PathVariable Long id, @RequestBody Equipo body) {
         return repo.findById(id).map(eq -> {
             eq.setNombreEquipo(body.getNombreEquipo());
             eq.setDescripcion(body.getDescripcion());
             return repo.save(eq);
         }).orElse(null);
     }

        // NUEVO: eliminar equipo (relaciones + equipo)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        equipoService.eliminarEquipo(id);
        return ResponseEntity.noContent().build();
    }
}

