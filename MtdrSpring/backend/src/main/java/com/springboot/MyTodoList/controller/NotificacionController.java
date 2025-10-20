package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Notificacion;
import com.springboot.MyTodoList.model.Usuario;
import com.springboot.MyTodoList.model.UsuarioEquipo;
import com.springboot.MyTodoList.repository.NotificacionRepository;
import com.springboot.MyTodoList.repository.UsuarioEquipoRepository;
import com.springboot.MyTodoList.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionRepository repo;
    private final UsuarioRepository usuarioRepo;
    private final UsuarioEquipoRepository ueRepo;

    public NotificacionController(NotificacionRepository repo,
                                  UsuarioRepository usuarioRepo,
                                  UsuarioEquipoRepository ueRepo) {
        this.repo = repo;
        this.usuarioRepo = usuarioRepo;
        this.ueRepo = ueRepo;
    }

    // ====== Helpers ======
    private Long resolveUserIdFromHeaders(String xUserId, String xUserEmail) {
        if (xUserId != null && !xUserId.isBlank()) {
            try { return Long.parseLong(xUserId.trim()); } catch (NumberFormatException ignored) {}
        }
        if (xUserEmail != null && !xUserEmail.isBlank()) {
            Usuario u = usuarioRepo.findByEmailIgnoreCase(xUserEmail.trim()).orElse(null);
            return u != null ? u.getId() : null;
        }
        return null;
    }

    private Usuario resolveUser(String xUserId, String xUserEmail) {
        Long uid = resolveUserIdFromHeaders(xUserId, xUserEmail);
        return (uid == null) ? null : usuarioRepo.findById(uid).orElse(null);
    }

    private static boolean hasCreatePermission(Usuario u) {
        if (u == null || u.getRol() == null) return false;
        String r = u.getRol().trim().toUpperCase(Locale.ROOT).replaceAll("\\s+","_");
        return r.equals("SUPER_ADMIN") || r.equals("SCRUM_MASTER");
    }

    private static boolean isScrumMaster(Usuario u) {
        if (u == null || u.getRol() == null) return false;
        String r = u.getRol().trim().toUpperCase(Locale.ROOT).replaceAll("\\s+","_");
        return r.equals("SCRUM_MASTER");
    }

    // ====== DTO ======
    public static class CreateNotificationDTO {
        public String titulo;
        public String mensaje;
        public Long equipoId;     // requerido
        public String prioridad;  // opcional
        public String tipo;       // opcional
    }

    // ====== Endpoints ======

    // Listado para el usuario actual (por header o por query param)
    @GetMapping
    public ResponseEntity<?> listMine(
        @RequestHeader(value = "X-User-Id", required = false) String xUserId,
        @RequestHeader(value = "X-User-Email", required = false) String xUserEmail,
        @RequestParam(value = "usuarioId", required = false) Long usuarioId,
        @RequestParam(value = "unreadOnly", required = false, defaultValue = "false") boolean unreadOnly
    ) {
        Long uid = (usuarioId != null) ? usuarioId : resolveUserIdFromHeaders(xUserId, xUserEmail);
        if (uid == null) return ResponseEntity.badRequest().body("Falta usuarioId o X-User-Id/X-User-Email");

        List<Notificacion> list = unreadOnly
            ? repo.findByUsuarioIdAndLeidaOrderByFechaEnvioDesc(uid, "N")
            : repo.findByUsuarioIdOrderByFechaEnvioDesc(uid);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listByPath(@PathVariable Long usuarioId,
        @RequestParam(value = "unreadOnly", required = false, defaultValue = "false") boolean unreadOnly) {
        List<Notificacion> list = unreadOnly
            ? repo.findByUsuarioIdAndLeidaOrderByFechaEnvioDesc(usuarioId, "N")
            : repo.findByUsuarioIdOrderByFechaEnvioDesc(usuarioId);
        return ResponseEntity.ok(list);
    }

    // Contador de no leídas
    @GetMapping("/count-unread")
    public ResponseEntity<?> countUnread(
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestHeader(value = "X-User-Email", required = false) String xUserEmail
    ) {
        Long uid = resolveUserIdFromHeaders(xUserId, xUserEmail);
        if (uid == null) return ResponseEntity.badRequest().body("Falta X-User-Id o X-User-Email");
        long c = repo.countByUsuarioIdAndLeida(uid, "N");
        Map<String, Object> resp = new HashMap<>();
        resp.put("unread", c);
        return ResponseEntity.ok(resp);
    }

    // Marcar una como leída
    @PatchMapping("/{id}/leer")
    public ResponseEntity<?> markRead(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestHeader(value = "X-User-Email", required = false) String xUserEmail
    ) {
        Long uid = resolveUserIdFromHeaders(xUserId, xUserEmail);
        if (uid == null) return ResponseEntity.badRequest().body("Falta X-User-Id o X-User-Email");

        return repo.findById(id).map(n -> {
            if (!Objects.equals(n.getUsuarioId(), uid)) {
                return ResponseEntity.status(403).body("No puedes modificar esta notificación");
            }
            n.setLeida("S");
            n.setFechaLeido(OffsetDateTime.now());
            repo.save(n);
            return ResponseEntity.ok().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Marcar todas como leídas
    @PatchMapping("/leer-todas")
    public ResponseEntity<?> markAllRead(
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestHeader(value = "X-User-Email", required = false) String xUserEmail
    ) {
        Long uid = resolveUserIdFromHeaders(xUserId, xUserEmail);
        if (uid == null) return ResponseEntity.badRequest().body("Falta X-User-Id o X-User-Email");

        List<Notificacion> noLeidas = repo.findByUsuarioIdAndLeidaOrderByFechaEnvioDesc(uid, "N");
        noLeidas.forEach(n -> { n.setLeida("S"); n.setFechaLeido(OffsetDateTime.now()); });
        repo.saveAll(noLeidas);
        return ResponseEntity.ok().build();
    }

    // === CREAR NOTIFICACION (SUPER_ADMIN o SCRUM_MASTER) ===
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody CreateNotificationDTO body,
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestHeader(value = "X-User-Email", required = false) String xUserEmail
    ) {
        Usuario creador = resolveUser(xUserId, xUserEmail);
        if (creador == null) return ResponseEntity.badRequest().body("Usuario no resuelto");
        if (!hasCreatePermission(creador)) return ResponseEntity.status(403).body("Sin permiso");

        if (body == null || body.equipoId == null) {
            return ResponseEntity.badRequest().body("equipoId es obligatorio");
        }

        // Si es SCRUM_MASTER, validar que participa en ese equipo
        if (isScrumMaster(creador)) {
            boolean pertenece = ueRepo.findByEquipoId(body.equipoId).stream()
                    .anyMatch(rel -> Objects.equals(rel.getId().getUsuarioId(), creador.getId()));
            if (!pertenece) {
                return ResponseEntity.status(403).body("SCRUM_MASTER no pertenece a este equipo");
            }
        }

        String titulo = (body.titulo == null || body.titulo.isBlank()) ? "Notificación" : body.titulo.trim();
        String mensaje = (body.mensaje == null) ? "" : body.mensaje.trim();

        // Miembros del equipo (fan-out)
        List<UsuarioEquipo> miembros = ueRepo.findByEquipoId(body.equipoId);
        if (miembros == null || miembros.isEmpty()) {
            return ResponseEntity.badRequest().body("El equipo no tiene miembros");
        }

        // Conjunto de destinatarios: todos los miembros + el creador (B)
        Set<Long> destinatarios = miembros.stream()
                .map(r -> r.getId().getUsuarioId())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        destinatarios.add(creador.getId()); // incluir creador por la opción B

        OffsetDateTime now = OffsetDateTime.now();
        List<Notificacion> toSave = destinatarios.stream().map(uid -> {
            Notificacion n = new Notificacion();
            n.setUsuarioId(uid);
            n.setTitulo(titulo);
            n.setMensaje(mensaje);
            n.setTipo(body.tipo);
            n.setPrioridad(body.prioridad);
            n.setFechaEnvio(now);
            n.setLeida("N");
            n.setEquipoId(body.equipoId);
            n.setCreadaPor(creador.getId());
            return n;
        }).collect(Collectors.toList());

        repo.saveAll(toSave);
        Map<String, Object> resp = new HashMap<>();
        resp.put("created", toSave.size());
        resp.put("equipoId", body.equipoId);
        return ResponseEntity.ok(resp);
    }
}
