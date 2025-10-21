package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Notificacion;
import com.springboot.MyTodoList.model.Usuario;
import com.springboot.MyTodoList.repository.NotificacionRepository;
import com.springboot.MyTodoList.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {

    private final NotificacionRepository repo;
    private final UsuarioRepository usuarios;

    public NotificacionController(NotificacionRepository repo, UsuarioRepository usuarios) {
        this.repo = repo;
        this.usuarios = usuarios;
    }

    // --- Helpers
    private Long resolveUserId(Long usuarioId, String emailHeader, String emailParam) {
        if (usuarioId != null) return usuarioId;

        final String email = (emailHeader != null && !emailHeader.trim().isEmpty())
                ? emailHeader.trim()
                : (emailParam != null ? emailParam.trim() : null);

        if (email == null || email.isEmpty()) return null;

        return usuarios.findByEmailIgnoreCase(email)
                .map(Usuario::getId) // tu entidad expone getId(); también tienes getUsuarioId si prefieres
                .orElse(null);
    }

    private static List<String> parseTipos(String csv) {
        if (csv == null || csv.trim().isEmpty()) return Collections.emptyList();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    // === LISTAR con filtros ===
    // Parámetros:
    //   usuarioId (opcional)
    //   email (opcional) ó header X-User-Email
    //   estado = unread|read|all
    //   tipos = "Proyectos,Tareas" (opcional)
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) String email,
            @RequestHeader(value = "X-User-Email", required = false) String emailHeader,
            @RequestParam(required = false, defaultValue = "unread") String estado,
            @RequestParam(required = false) String tipos) {

        Long uid = resolveUserId(usuarioId, emailHeader, email);
        if (uid == null) {
            return ResponseEntity.badRequest().body("Falta usuarioId o email");
        }

        String leida = null;
        switch (estado.toLowerCase(Locale.ROOT)) {
            case "unread": leida = "N"; break;
            case "read":   leida = "Y"; break;
            case "all":    leida = null; break;
            default:       leida = "N";
        }

        List<String> filtroTipos = parseTipos(tipos);

        List<Notificacion> out;
        boolean hayTipos = filtroTipos != null && !filtroTipos.isEmpty();

        if (!hayTipos) {
            // Sin filtro de tipos
            if (leida == null) {
                out = repo.findByUsuarioIdOrderByFechaEnvioDesc(uid);
            } else {
                out = repo.findByUsuarioIdAndLeidaOrderByFechaEnvioDesc(uid, leida);
            }
        } else {
            // Con filtro de tipos
            if (leida == null) {
                out = repo.findByUsuarioIdAndTipoInOrderByFechaEnvioDesc(uid, filtroTipos);
            } else {
                out = repo.findByUsuarioIdAndLeidaAndTipoInOrderByFechaEnvioDesc(uid, leida, filtroTipos);
            }
        }

        return ResponseEntity.ok(out);
    }

    // === CREAR (para pruebas o si las generas desde backend) ===
    @PostMapping
    public Notificacion create(@RequestBody Notificacion n,
                               @RequestHeader(value = "X-User-Email", required = false) String emailHeader) {
        if (n.getUsuarioId() == null && emailHeader != null) {
            usuarios.findByEmailIgnoreCase(emailHeader).ifPresent(u -> n.setUsuarioId(u.getId()));
        }
        if (n.getFechaEnvio() == null) n.setFechaEnvio(OffsetDateTime.now());
        if (n.getLeida() == null) n.setLeida("N");
        return repo.save(n);
    }

    // === MARCAR 1 como leído ===
    @PatchMapping("/{id}/leer")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        return repo.findById(id).map(n -> {
            n.setLeida("Y");
            n.setFechaLeido(OffsetDateTime.now());
            return ResponseEntity.ok(repo.save(n));
        }).orElse(ResponseEntity.notFound().build());
    }

    // === MARCAR TODAS como leídas (del usuario) ===
    @PatchMapping("/leer-todas")
    public ResponseEntity<?> markAllAsRead(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) String email,
            @RequestHeader(value = "X-User-Email", required = false) String emailHeader) {

        Long uid = resolveUserId(usuarioId, emailHeader, email);
        if (uid == null) return ResponseEntity.badRequest().body("Falta usuarioId o email");

        List<Notificacion> list = repo.findByUsuarioIdAndLeidaOrderByFechaEnvioDesc(uid, "N");
        OffsetDateTime now = OffsetDateTime.now();
        for (Notificacion n : list) {
            n.setLeida("Y");
            n.setFechaLeido(now);
        }
        repo.saveAll(list);
        return ResponseEntity.ok(Map.of("updated", list.size()));
    }
}
