package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Proyecto;
import com.springboot.MyTodoList.repository.ProyectoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proyectos")
@CrossOrigin(origins = "*")
public class ProyectoController {

    private final ProyectoRepository repo;

    public ProyectoController(ProyectoRepository repo) {
        this.repo = repo;
    }

    // === TODOS (si aún lo quieres conservar para admin/debug) ===
    @GetMapping
    public List<Proyecto> all() {
        return repo.findAll();
    }

    // === SOLO MÍOS ===
    @GetMapping("/mios/{usuarioId}")
    public List<Proyecto> myProjects(@PathVariable Long usuarioId) {
        return repo.findByCreadorId(usuarioId);
    }

    @GetMapping("/{id}")
    public Proyecto one(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @PostMapping
    public Proyecto create(@RequestBody Proyecto p, @RequestHeader(value="X-User-Id", required=false) Long userIdHeader) {
        // Estado default
        if (p.getEstado() == null || p.getEstado().trim().isEmpty()) {
            p.setEstado("Pendiente");
        }
        // === Setear creador ===
        // 1) Si te mandan el creadorId en el body, se respeta; si no, intenta por header X-User-Id;
        // 2) Si no hay ninguno, lo dejamos nulo (pero idealmente SIEMPRE mándalo).
        if (p.getCreadorId() == null && userIdHeader != null) {
            p.setCreadorId(userIdHeader);
        }
        return repo.save(p);
    }

    @PutMapping("/{id}")
    public Proyecto update(@PathVariable Long id, @RequestBody Proyecto p) {
        return repo.findById(id).map(actual -> {
            actual.setNombreProyecto(p.getNombreProyecto());
            actual.setDescripcion(p.getDescripcion());
            actual.setEstado(p.getEstado());
            actual.setEquipoId(p.getEquipoId());
            actual.setFechaInicio(p.getFechaInicio());
            actual.setFechaFin(p.getFechaFin());
            actual.setUltimoAcceso(p.getUltimoAcceso());
            // No cambiamos creadorId en update para mantener autoría
            return repo.save(actual);
        }).orElseGet(() -> {
            // Si no existe, lo creamos con el id provisto (no usual, pero mantiene tu patrón actual)
            p.setId(id);
            return repo.save(p);
        });
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
