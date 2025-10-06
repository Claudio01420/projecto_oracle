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
        // Fuerza estado "Pendiente" si viene vacío
        if (p.getEstado() == null || p.getEstado().trim().isEmpty()) {
            p.setEstado("Pendiente");
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
            return repo.save(actual);
        }).orElseGet(() -> {
            p.setId(id);
            return repo.save(p);
        });
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
