// NotificacionController.java  (REEMPLAZA COMPLETO)

package com.springboot.MyTodoList.controller;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.model.Notificacion;
import com.springboot.MyTodoList.repository.UsuarioRepository;
import com.springboot.MyTodoList.service.NotificacionService;

@RestController
@RequestMapping("/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {

    private final NotificacionService service;
    private final UsuarioRepository usuarioRepo;

    public NotificacionController(NotificacionService service,
                                  UsuarioRepository usuarioRepo) {
        this.service = service;
        this.usuarioRepo = usuarioRepo;
    }

    /** Resuelve el ID de usuario desde ?usuarioId, X-User-Id o X-User-Email */
    private Long resolveUserId(Long usuarioId, Long hUserId, String hEmail) {
        if (usuarioId != null) return usuarioId;
        if (hUserId != null)   return hUserId;
        if (hEmail != null && !hEmail.isBlank()) {
            return usuarioRepo.findByEmailIgnoreCase(hEmail.trim())
                    .map(u -> {
                        try {
                            Method m = u.getClass().getMethod("getId");
                            Object v = m.invoke(u);
                            if (v instanceof Long) return (Long) v;
                        } catch (Exception ignored) {}
                        try {
                            Method m = u.getClass().getMethod("getUsuarioId");
                            Object v = m.invoke(u);
                            if (v instanceof Long) return (Long) v;
                        } catch (Exception ignored) {}
                        return null;
                    })
                    .orElse(null);
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> listar(
            @RequestParam(required = false) Long usuarioId,
            @RequestHeader(value = "X-User-Id", required = false) Long hUserId,
            @RequestHeader(value = "X-User-Email", required = false) String hEmail
    ){
        Long id = resolveUserId(usuarioId, hUserId, hEmail);
        if (id == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(service.listarPorUsuario(id));
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<Void> leer(@PathVariable Long id){
        service.marcarLeida(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/leer-todas")
    public ResponseEntity<Void> leerTodas(
            @RequestParam(required = false) Long usuarioId,
            @RequestHeader(value = "X-User-Id", required = false) Long hUserId,
            @RequestHeader(value = "X-User-Email", required = false) String hEmail
    ){
        Long id = resolveUserId(usuarioId, hUserId, hEmail);
        if (id == null) return ResponseEntity.badRequest().build();
        service.marcarTodasLeidas(id);
        return ResponseEntity.noContent().build();
    }

    // ===== NUEVOS: BORRAR =====

    /** Borra una notificación del usuario autenticado (propietario). */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(
            @PathVariable Long id,
            @RequestParam(required = false) Long usuarioId,
            @RequestHeader(value = "X-User-Id", required = false) Long hUserId,
            @RequestHeader(value = "X-User-Email", required = false) String hEmail
    ) {
        Long uid = resolveUserId(usuarioId, hUserId, hEmail);
        if (uid == null) return ResponseEntity.badRequest().body("usuarioId requerido");
        boolean ok = service.borrar(id, uid);
        return ok ? ResponseEntity.noContent().build()
                  : ResponseEntity.status(403).body("No puedes borrar esta notificación");
    }

    /** Borra todas las notificaciones del usuario. */
    @DeleteMapping("/todas")
    public ResponseEntity<?> borrarTodas(
            @RequestParam(required = false) Long usuarioId,
            @RequestHeader(value = "X-User-Id", required = false) Long hUserId,
            @RequestHeader(value = "X-User-Email", required = false) String hEmail
    ) {
        Long uid = resolveUserId(usuarioId, hUserId, hEmail);
        if (uid == null) return ResponseEntity.badRequest().body("usuarioId requerido");
        long deleted = service.borrarTodas(uid);
        return ResponseEntity.ok().body("{\"deleted\":"+deleted+"}");
    }
}

