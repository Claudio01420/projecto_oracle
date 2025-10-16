package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Proyecto;
import com.springboot.MyTodoList.repository.ProyectoRepository;
import com.springboot.MyTodoList.repository.TareaRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/proyectos")
@CrossOrigin(origins = "*")
public class ProyectoController {

    private final ProyectoRepository repo;
    private final TareaRepository tareaRepo;

    public ProyectoController(ProyectoRepository repo, TareaRepository tareaRepo) {
        this.repo = repo;
        this.tareaRepo = tareaRepo;
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

    // Nuevo endpoint: último proyecto + conteos de tareas y días restantes
    @GetMapping("/ultimo")
    public Map<String, Object> ultimoProyecto() {
        Map<String, Object> resp = new HashMap<>();
        Proyecto p = repo.findTopByOrderByIdDesc();
        if (p == null) {
            return resp;
        }
        resp.put("projectId", p.getId());
        resp.put("projectName", p.getNombreProyecto());
        resp.put("equipoId", p.getEquipoId()); // <-- agregado: devuelve equipoId
        // días restantes (puede ser negativo si ya pasó la fecha)
        if (p.getFechaFin() != null) {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), p.getFechaFin());
            resp.put("daysRemaining", days);
        } else {
            resp.put("daysRemaining", null);
        }
        long total = tareaRepo.countByProjectId(p.getId());
        long completed = tareaRepo.countCompletedByProjectId(p.getId());
        resp.put("totalTasks", total);
        resp.put("completedTasks", completed);
        return resp;
    }

    // Nuevo: proyectos del usuario con stats (progreso, fechas)
    @GetMapping("/mios/{usuarioId}/stats")
    public List<Map<String, Object>> myProjectsWithStats(@PathVariable Long usuarioId) {
        List<Proyecto> proyectos = repo.findByCreadorId(usuarioId);
        return proyectos.stream().map(p -> {
            Map<String,Object> m = new HashMap<>();
            m.put("projectId", p.getId());
            m.put("projectName", p.getNombreProyecto());
            m.put("fechaInicio", p.getFechaInicio()); // LocalDate -> JSON "YYYY-MM-DD"
            m.put("fechaFin", p.getFechaFin());
            long total = tareaRepo.countByProjectId(p.getId());
            long completed = tareaRepo.countCompletedByProjectId(p.getId());
            double progress = total == 0 ? 0.0 : ((double) completed / (double) total) * 100.0;
            // redondeo a 2 decimales
            double progressRounded = Math.round(progress * 100.0) / 100.0;
            m.put("totalTasks", total);
            m.put("completedTasks", completed);
            m.put("progressPercent", progressRounded);
            return m;
        }).collect(Collectors.toList());
    }
}
