package com.springboot.MyTodoList.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.model.Equipo;
import com.springboot.MyTodoList.model.Proyecto;
import com.springboot.MyTodoList.model.RolEquipo;
import com.springboot.MyTodoList.model.UsuarioEquipo;
import com.springboot.MyTodoList.repository.EquipoRepository;
import com.springboot.MyTodoList.repository.ProyectoRepository;
import com.springboot.MyTodoList.repository.TareaRepository;
import com.springboot.MyTodoList.repository.UsuarioEquipoRepository;
import com.springboot.MyTodoList.service.EquipoService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/equipos")
public class EquipoController {

    private final EquipoRepository repo;
    private final EquipoService equipoService;
    private final ProyectoRepository proyectoRepo;
    private final TareaRepository tareaRepo;
    private final UsuarioEquipoRepository ueRepo; // para el filtro adminId

    public EquipoController(EquipoRepository repo,
                            EquipoService equipoService,
                            ProyectoRepository proyectoRepo,
                            TareaRepository tareaRepo,
                            UsuarioEquipoRepository ueRepo) {
        this.repo = repo;
        this.equipoService = equipoService;
        this.proyectoRepo = proyectoRepo;
        this.tareaRepo = tareaRepo;
        this.ueRepo = ueRepo;
    }

    // GET con soporte a ?adminId=...  → solo devuelve equipos donde ese usuario es ADMIN
    @GetMapping
    public List<Equipo> all(@RequestParam(value = "adminId", required = false) Long adminId) {
        if (adminId == null) {
            // AJUSTE: devolver ordenado por nombre para la UI (no rompe compatibilidad)
            return repo.findAll(Sort.by(Sort.Direction.ASC, "nombreEquipo"));
        }
        List<UsuarioEquipo> rels = ueRepo.findByUsuarioId(adminId);
        Set<Long> adminEquipoIds = rels.stream()
                .filter(ue -> ue.getRol() == RolEquipo.ADMIN && ue.getId() != null)
                .map(ue -> ue.getId().getEquipoId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (adminEquipoIds.isEmpty()) return Collections.emptyList();
        // Filtramos por IDs
        return repo.findAll().stream()
                .filter(eq -> adminEquipoIds.contains(eq.getId()))
                .sorted(Comparator.comparing(e -> Optional.ofNullable(e.getNombreEquipo()).orElse("")))
                .collect(Collectors.toList());
    }

    // OPCIONAL: equipos donde el usuario es miembro (cualquier rol)
    // Útil si prefieres poblar el combo del Scrum Master con un solo request.
    @GetMapping("/member/{usuarioId}")
    public List<Equipo> listMemberTeams(@PathVariable Long usuarioId) {
        List<UsuarioEquipo> rels = ueRepo.findByUsuarioId(usuarioId);
        if (rels == null || rels.isEmpty()) return Collections.emptyList();
        Set<Long> equipoIds = rels.stream()
                .filter(ue -> ue.getId() != null)
                .map(ue -> ue.getId().getEquipoId())
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (equipoIds.isEmpty()) return Collections.emptyList();
        List<Equipo> equipos = repo.findAllById(equipoIds);
        equipos.sort(Comparator.comparing(e -> Optional.ofNullable(e.getNombreEquipo()).orElse("")));
        return equipos;
    }

    // OPCIONAL (para optimizar): GET /equipos/by-ids?ids=1,2,3
    @GetMapping("/by-ids")
    public List<Equipo> byIds(@RequestParam("ids") List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        List<Equipo> equipos = repo.findAllById(ids);
        equipos.sort(Comparator.comparing(e -> Optional.ofNullable(e.getNombreEquipo()).orElse("")));
        return equipos;
    }

    @GetMapping("/{id}")
    public Equipo one(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @PostMapping
    public Equipo create(@RequestBody Equipo e) {
        return repo.save(e);
    }

    // Crear equipo y vincular creador como ADMIN
    @PostMapping("/crear-con-admin")
    public Equipo crearConAdmin(@RequestParam("creadorId") Long creadorId, @RequestBody Equipo e) {
        return equipoService.crearEquipoConAdmin(e, creadorId);
    }

    @PutMapping("/{id}")
    public Equipo update(@PathVariable Long id, @RequestBody Equipo body) {
        return repo.findById(id).map(eq -> {
            eq.setNombreEquipo(body.getNombreEquipo());
            eq.setDescripcion(body.getDescripcion());
            return repo.save(eq);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        equipoService.eliminarEquipo(id);
        return ResponseEntity.noContent().build();
    }

    // ICL del equipo (como lo tenías)
    @GetMapping("/{id}/icl")
    public ResponseEntity<Map<String,Object>> getEquipoIcl(@PathVariable Long id) {
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

        Double estimatedHours = tareaRepo.sumEstimatedHoursByProjectIdIn(projectIds);
        if (estimatedHours == null) estimatedHours = 0.0;
        Double realHours = tareaRepo.sumRealHoursByProjectIdIn(projectIds);
        if (realHours == null) realHours = 0.0;

        long activeTasks = tareaRepo.countByProjectIdInAndCompletedAtIsNull(projectIds);
        long completedTasks = tareaRepo.countByProjectIdInAndStatusIgnoreCase(projectIds, "Hecho");

        double hoursRatio;
        if (estimatedHours == 0.0) {
            hoursRatio = realHours > 0.0 ? realHours : 1.0;
        } else {
            hoursRatio = realHours / estimatedHours;
        }
        double tasksFactor = (double) activeTasks / ((double) completedTasks + 1.0);
        double icl = hoursRatio * tasksFactor;

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
