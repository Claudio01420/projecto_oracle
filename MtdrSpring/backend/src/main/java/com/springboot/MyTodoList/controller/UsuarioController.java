package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Usuario;
import com.springboot.MyTodoList.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

// nuevas imports
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@CrossOrigin(origins = "*") // ajusta en producción
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository repo;

    public UsuarioController(UsuarioRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> all(@RequestHeader(value = "X-User-Role", required = false) String callerRole) {
        if (!hasAnyRole(callerRole, Usuario.ROLE_SUPER_ADMIN, Usuario.ROLE_SCRUM_MASTER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Usuario> list = repo.findAll();
        list.forEach(u -> u.setContrasenia(null));
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> one(@PathVariable Long id,
                                      @RequestHeader(value = "X-User-Role", required = false) String callerRole,
                                      @RequestHeader(value = "X-User-Id", required = false) String callerIdHeader) {

        // Super Admin / Scrum Master -> pueden ver cualquiera
        if (hasAnyRole(callerRole, Usuario.ROLE_SUPER_ADMIN, Usuario.ROLE_SCRUM_MASTER)) {
            Optional<Usuario> u = repo.findById(id);
            if (u.isPresent()) {
                Usuario user = u.get();
                user.setContrasenia(null);
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        // Desarrollador -> solo su propio id
        if (hasAnyRole(callerRole, Usuario.ROLE_DEVELOPER)) {
            try {
                Long callerId = callerIdHeader != null ? Long.parseLong(callerIdHeader) : null;
                if (callerId != null && callerId.equals(id)) {
                    Optional<Usuario> u = repo.findById(id);
                    if (u.isPresent()) {
                        Usuario user = u.get();
                        user.setContrasenia(null);
                        return ResponseEntity.ok(user);
                    } else {
                        return ResponseEntity.notFound().build();
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            } catch (NumberFormatException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    public ResponseEntity<Usuario> create(@RequestBody Usuario u,
                                          @RequestHeader(value = "X-User-Role", required = false) String callerRole) {
        // autorización
        if (!hasAnyRole(callerRole, Usuario.ROLE_SUPER_ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (u == null ||
            u.getEmail() == null || u.getEmail().trim().isEmpty() ||
            u.getContrasenia() == null || u.getContrasenia().isEmpty() ||
            u.getNombre() == null || u.getNombre().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String email = u.getEmail().trim();
        // si ya existe usuario con el mismo email (case-insensitive)
        if (repo.findByEmailIgnoreCase(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // asignar rol por defecto si no viene: Desarrollador
        if (u.getRol() == null || u.getRol().trim().isEmpty()) {
            u.setRol(Usuario.ROLE_DEVELOPER);
        }

        Usuario saved = repo.save(u);
        // no devolver la contraseña
        saved.setContrasenia(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Login: recibe { "email": "...", "contrasenia": "..." }
     * - Email case-insensitive
     * - No devuelve la contraseña en la respuesta
     */
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Usuario credentials) {
        if (credentials == null ||
            credentials.getEmail() == null || credentials.getEmail().trim().isEmpty() ||
            credentials.getContrasenia() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String email = credentials.getEmail().trim();
        String pass  = credentials.getContrasenia();

        Optional<Usuario> opt = repo.findByEmailIgnoreCase(email);
        if (opt.isPresent() && pass.equals(opt.get().getContrasenia())) {
            Usuario user = opt.get();
            // no exponer contraseña
            user.setContrasenia(null);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Helper para comprobar roles (null seguro)
    private boolean hasAnyRole(String callerRole, String... allowedRoles) {
        if (callerRole == null) return false;
        for (String ar : allowedRoles) {
            if (callerRole.equalsIgnoreCase(ar)) return true;
        }
        return false;
    }

    // Mapa en memoria para tokens temporales (token -> TokenInfo)
    private static final Map<String, TokenInfo> resetTokens = new ConcurrentHashMap<>();

    private static class TokenInfo {
        final String email;
        final Instant expiresAt;
        TokenInfo(String email, Instant expiresAt) {
            this.email = email;
            this.expiresAt = expiresAt;
        }
    }

    /**
     * Solicitud de reseteo:
     * Recibe { "email": "user@..." }
     * - Si el usuario existe: genera token temporal y lo devuelve (simulación de envío por email en dev).
     * - En producción se debe enviar email y NO devolver el token en la respuesta.
     */
    @PostMapping("/reset-request")
    public ResponseEntity<Map<String,String>> resetRequest(@RequestBody Map<String,String> body) {
        if (body == null || body.get("email") == null || body.get("email").trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","email requerido"));
        }
        String email = body.get("email").trim();
        Optional<Usuario> opt = repo.findByEmailIgnoreCase(email);
        if (opt.isPresent()) {
            String token = UUID.randomUUID().toString().replace("-", "");
            Instant expires = Instant.now().plus(15, ChronoUnit.MINUTES);
            resetTokens.put(token, new TokenInfo(email.toLowerCase(), expires));
            // RESPUESTA: en desarrollo devolvemos el token para que puedas probar. En producción envía email.
            return ResponseEntity.ok(Map.of("message","token generado (dev)", "token", token));
        }
        // No revelar si el email existe: devolver mensaje genérico
        return ResponseEntity.ok(Map.of("message","Si la cuenta existe, se ha enviado un correo con instrucciones"));
    }

    /**
     * Confirmar reseteo:
     * Recibe { "email": "...", "token": "...", "newPassword": "..." }
     */
    @PostMapping("/reset-confirm")
    public ResponseEntity<Map<String,String>> resetConfirm(@RequestBody Map<String,String> body) {
        if (body == null ||
            body.get("email") == null || body.get("email").trim().isEmpty() ||
            body.get("token") == null || body.get("token").trim().isEmpty() ||
            body.get("newPassword") == null || body.get("newPassword").isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","email, token y nueva contraseña requeridos"));
        }

        String email = body.get("email").trim().toLowerCase();
        String token = body.get("token").trim();
        String newPassword = body.get("newPassword");

        TokenInfo ti = resetTokens.get(token);
        if (ti == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","token inválido o expirado"));
        }
        if (!ti.email.equalsIgnoreCase(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","token no corresponde al email"));
        }
        if (ti.expiresAt.isBefore(Instant.now())) {
            resetTokens.remove(token);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","token expirado"));
        }

        Optional<Usuario> opt = repo.findByEmailIgnoreCase(email);
        if (opt.isEmpty()) {
            // raro: usuario no existe aunque token exista -> retirar token
            resetTokens.remove(token);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","usuario no encontrado"));
        }

        Usuario u = opt.get();
        u.setContrasenia(newPassword);
        repo.save(u);
        resetTokens.remove(token);
        return ResponseEntity.ok(Map.of("message","contraseña actualizada"));
    }
}

