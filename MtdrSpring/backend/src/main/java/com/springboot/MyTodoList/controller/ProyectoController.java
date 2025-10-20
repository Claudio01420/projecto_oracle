package com.springboot.MyTodoList.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.model.Proyecto;
import com.springboot.MyTodoList.model.UsuarioEquipo;
import com.springboot.MyTodoList.repository.ProyectoRepository;
import com.springboot.MyTodoList.repository.TareaRepository;
import com.springboot.MyTodoList.repository.UsuarioEquipoRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/proyectos")
public class ProyectoController {

    private final ProyectoRepository repo;
    private final TareaRepository tareaRepo;
    private final UsuarioEquipoRepository ueRepo; // para calcular proyectos visibles

    public ProyectoController(ProyectoRepository repo,
                              TareaRepository tareaRepo,
                              UsuarioEquipoRepository ueRepo) {
        this.repo = repo;
        this.tareaRepo = tareaRepo;
        this.ueRepo = ueRepo;
    }

    // LISTA general con soporte a ?equipoId=...
    @GetMapping
    public List<Proyecto> all(@RequestParam(value = "equipoId", required = false) Long equipoId) {
        if (equipoId != null) {
            return repo.findByEquipoId(equipoId);
        }
        return repo.findAll();
    }

    // SOLO míos (creador)
    @GetMapping("/mios/{usuarioId}")
    public List<Proyecto> myProjects(@PathVariable Long usuarioId) {
        return repo.findByCreadorId(usuarioId);
    }

    // PROYECTOS por equipo (rutas compatibles con tu JS)
    @GetMapping("/equipo/{equipoId}")
    public List<Proyecto> byEquipoPath(@PathVariable Long equipoId) {
        return repo.findByEquipoId(equipoId);
    }

    @GetMapping("/por-equipo/{equipoId}")
    public List<Proyecto> byEquipoAlt(@PathVariable Long equipoId) {
        return repo.findByEquipoId(equipoId);
    }

    // NUEVO: PROYECTOS VISIBLES para un usuario:
    // Unión de: creados por mí + de equipos donde soy miembro (cualquier rol)
    @GetMapping("/visibles/{usuarioId}")
    public List<Proyecto> visibles(@PathVariable Long usuarioId) {
        Map<Long, Proyecto> byId = new LinkedHashMap<>();

        // A) Creados por el usuario
        for (Proyecto p : repo.findByCreadorId(usuarioId)) {
            if (p != null && p.getId() != null) byId.putIfAbsent(p.getId(), p);
        }

        // B) Equipos donde es miembro
        List<UsuarioEquipo> rels = ueRepo.findByUsuarioId(usuarioId);
        Set<Long> equipoIds = rels.stream()
                .filter(Objects::nonNull)
                .map(ue -> ue.getId() != null ? ue.getId().getEquipoId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        for (Long eid : equipoIds) {
            for (Proyecto p : repo.findByEquipoId(eid)) {
                if (p != null && p.getId() != null) byId.putIfAbsent(p.getId(), p);
            }
        }

        return new ArrayList<>(byId.values());
    }

    // ✅✅ NUEVO: AUTOCOMPLETE /picker
    // Soporta /proyectos/buscar y /proyectos/search
    // Si llega usuarioId, se limita a proyectos "visibles" para ese usuario.
    @GetMapping({"/buscar", "/search"})
    public List<Proyecto> buscar(
            @RequestParam("q") String q,
            @RequestParam(value = "usuarioId", required = false) Long usuarioId
    ) {
        if (q == null || q.trim().length() < 2) return Collections.emptyList();
        final String needle = q.trim().toLowerCase();

        // Si tenemos usuarioId, usamos "visibles(usuarioId)" y filtramos por nombre
        if (usuarioId != null) {
            return visibles(usuarioId).stream()
                    .filter(Objects::nonNull)
                    .filter(p -> {
                        String name = p.getNombreProyecto();
                        return name != null && name.toLowerCase().contains(needle);
                    })
                    .sorted(Comparator.comparing(
                            p -> Optional.ofNullable(p.getNombreProyecto()).orElse(""),
                            String.CASE_INSENSITIVE_ORDER
                    ))
                    .limit(10)
                    .collect(Collectors.toList());
        }

        // Sin usuarioId: intenta usar Top10 por nombre (si no, fallback a all + filtro)
        List<Proyecto> top = repo.findTop10ByNombreProyectoContainingIgnoreCase(q.trim());
        if (top != null && !top.isEmpty()) {
            // Asegura orden estable por nombre
            return top.stream()
                    .sorted(Comparator.comparing(
                            p -> Optional.ofNullable(p.getNombreProyecto()).orElse(""),
                            String.CASE_INSENSITIVE_ORDER
                    ))
                    .collect(Collectors.toList());
        }

        // Fallback (no debería ser necesario, pero es inofensivo)
        return repo.findAll().stream()
                .filter(Objects::nonNull)
                .filter(p -> {
                    String name = p.getNombreProyecto();
                    return name != null && name.toLowerCase().contains(needle);
                })
                .sorted(Comparator.comparing(
                        p -> Optional.ofNullable(p.getNombreProyecto()).orElse(""),
                        String.CASE_INSENSITIVE_ORDER
                ))
                .limit(10)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Proyecto one(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @PostMapping
    public Proyecto create(@RequestBody Proyecto p,
                           @RequestHeader(value = "X-User-Id", required = false) Long userIdHeader) {
        if (p.getEstado() == null || p.getEstado().trim().isEmpty()) {
            p.setEstado("Pendiente");
        }
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

    // ===== Helpers dashboard existentes =====

    @GetMapping("/ultimo")
    public Map<String, Object> ultimoProyecto() {
        Map<String, Object> resp = new HashMap<>();
        Proyecto p = repo.findTopByOrderByIdDesc();
        if (p == null) return resp;

        resp.put("projectId", p.getId());
        resp.put("projectName", p.getNombreProyecto());
        resp.put("equipoId", p.getEquipoId());

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

    @GetMapping("/mios/{usuarioId}/stats")
    public List<Map<String, Object>> myProjectsWithStats(@PathVariable Long usuarioId) {
        List<Proyecto> proyectos = repo.findByCreadorId(usuarioId);
        return proyectos.stream().map(p -> {
            Map<String,Object> m = new HashMap<>();
            m.put("projectId", p.getId());
            m.put("projectName", p.getNombreProyecto());
            m.put("fechaInicio", p.getFechaInicio());
            m.put("fechaFin", p.getFechaFin());
            long total = tareaRepo.countByProjectId(p.getId());
            long completed = tareaRepo.countCompletedByProjectId(p.getId());
            double progress = total == 0 ? 0.0 : ((double) completed / (double) total) * 100.0;
            double progressRounded = Math.round(progress * 100.0) / 100.0;
            m.put("totalTasks", total);
            m.put("completedTasks", completed);
            m.put("progressPercent", progressRounded);
            return m;
        }).collect(Collectors.toList());
    }

    @GetMapping("/horas")
    public List<Map<String, Object>> horasPorProyecto() {
        List<Proyecto> proyectos = repo.findAll();
        return proyectos.stream().map(p -> {
            Map<String,Object> m = new HashMap<>();
            m.put("projectId", p.getId());
            m.put("projectName", p.getNombreProyecto());
            Double est = tareaRepo.sumEstimatedHoursByProjectIdIn(Collections.singletonList(p.getId()));
            Double real = tareaRepo.sumRealHoursByProjectIdIn(Collections.singletonList(p.getId()));
            m.put("estimatedHours", est == null ? 0.0 : est);
            m.put("realHours", real == null ? 0.0 : real);
            return m;
        }).collect(Collectors.toList());
    }
}
