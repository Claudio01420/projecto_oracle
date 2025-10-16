package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Equipo;
import com.springboot.MyTodoList.model.Proyecto;
import com.springboot.MyTodoList.repository.EquipoRepository;
import com.springboot.MyTodoList.repository.ProyectoRepository;
import com.springboot.MyTodoList.repository.TareaRepository;
import com.springboot.MyTodoList.service.EquipoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/equipos")
public class EquipoController {

    private final EquipoRepository repo;
    private final EquipoService equipoService;
    private final ProyectoRepository proyectoRepo;
    private final TareaRepository tareaRepo;

    public EquipoController(EquipoRepository repo, EquipoService equipoService,
                            ProyectoRepository proyectoRepo, TareaRepository tareaRepo) {
        this.repo = repo;
        this.equipoService = equipoService;
        this.proyectoRepo = proyectoRepo;
        this.tareaRepo = tareaRepo;
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

    // Nuevo: calcular ICL del equipo
    @GetMapping("/{id}/icl")
    public ResponseEntity<Map<String,Object>> getEquipoIcl(@PathVariable Long id) {
        // 1) Obtener proyectos del equipo
        List<Proyecto> proyectos = proyectoRepo.findByEquipoId(id);
        if (proyectos == null || proyectos.isEmpty()) {
            Map<String,Object> empty = new HashMap<>();
            empty.put("icl", 0.0);
            empty.put("risk", "Bajo");
            empty.put("message", "Sin proyectos asociados");
            empty.put("estimatedHours", 0.0);
            empty.put("realHours", 0.0);
            empty.put("activeTasks", 0);
            empty.put("completedTasks", 0);
            return ResponseEntity.ok(empty);
        }
        List<Long> projectIds = proyectos.stream().map(Proyecto::getId).collect(Collectors.toList());

        // 2) Sumar horas y contar tareas
        Double estimatedHours = tareaRepo.sumEstimatedHoursByProjectIdIn(projectIds);
        if (estimatedHours == null) estimatedHours = 0.0;
        Double realHours = tareaRepo.sumRealHoursByProjectIdIn(projectIds);
        if (realHours == null) realHours = 0.0;

        long activeTasks = tareaRepo.countByProjectIdInAndCompletedAtIsNull(projectIds);
        long completedTasks = tareaRepo.countByProjectIdInAndStatusIgnoreCase(projectIds, "Hecho");

        // 3) Calcular ICL según la fórmula:
        // ICL = (Horas reales / Horas estimadas) × (Tareas activas / (Tareas completadas + 1))
        double hoursRatio;
        if (estimatedHours == 0.0) {
            // heurística: si no hay estimado pero sí trabajo real, penalizamos proporcionalmente;
            // si no hay estimado y no hay real, ratio = 1 (neutral)
            hoursRatio = realHours > 0.0 ? realHours : 1.0;
        } else {
            hoursRatio = realHours / estimatedHours;
        }
        double tasksFactor = (double) activeTasks / ((double) completedTasks + 1.0);
        double icl = hoursRatio * tasksFactor;

        // 4) Determinar riesgo y mensaje
        String risk;
        String message;
        if (icl <= 0.9) {
            risk = "Bajo";
            message = "Carga laboral saludable";
        } else if (icl <= 1.2) {
            risk = "Medio";
            message = "Carga sostenida cercana al límite";
        } else {
            risk = "Alto";
            message = "Posible sobrecarga / fatiga laboral";
        }

        Map<String,Object> resp = new HashMap<>();
        resp.put("icl", icl);
        resp.put("risk", risk);
        resp.put("message", message);
        resp.put("estimatedHours", estimatedHours);
        resp.put("realHours", realHours);
        resp.put("activeTasks", activeTasks);
        resp.put("completedTasks", completedTasks);
        return ResponseEntity.ok(resp);
    }
}

