package com.springboot.MyTodoList.controller;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.model.Notificacion;
import com.springboot.MyTodoList.model.RolEquipo;
import com.springboot.MyTodoList.model.Usuario;
import com.springboot.MyTodoList.model.UsuarioEquipo;
import com.springboot.MyTodoList.model.UsuarioEquipoId;
import com.springboot.MyTodoList.repository.EquipoRepository;
import com.springboot.MyTodoList.repository.NotificacionRepository;
import com.springboot.MyTodoList.repository.UsuarioEquipoRepository;
import com.springboot.MyTodoList.repository.UsuarioRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/equipos")
public class EquipoMembersController {

    private final UsuarioRepository usuarioRepo;
    private final UsuarioEquipoRepository ueRepo;
    private final EquipoRepository equipoRepo;
    private final NotificacionRepository notifRepo;

    public EquipoMembersController(UsuarioRepository usuarioRepo,
                                   UsuarioEquipoRepository ueRepo,
                                   EquipoRepository equipoRepo,
                                   NotificacionRepository notifRepo) {
        this.usuarioRepo = usuarioRepo;
        this.ueRepo = ueRepo;
        this.equipoRepo = equipoRepo;
        this.notifRepo = notifRepo;
    }

    @GetMapping("/{equipoId}/miembros")
    public List<UsuarioEquipo> miembros(@PathVariable Long equipoId) {
        return ueRepo.findByEquipoId(equipoId);
    }

    // Invitar por correo -> crea NOTIFICACION tipo INVITACION_EQUIPO
    @PostMapping("/{equipoId}/invitar")
    public ResponseEntity<?> invitar(@PathVariable Long equipoId,
                                     @RequestParam("email") String email,
                                     @RequestParam("rol") RolEquipo rol) {

        if (!equipoRepo.existsById(equipoId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Equipo no existe");
        }

        Optional<Usuario> optUser = usuarioRepo.findByEmailIgnoreCase(email);
        if (optUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        Usuario u = optUser.get();

        // Si ya es miembro -> 409
        UsuarioEquipoId id = new UsuarioEquipoId(u.getId(), equipoId);
        if (ueRepo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya es miembro del equipo");
        }

        // Si ya tiene una NOTIFICACION pendiente (leida = 'N') para este equipo -> 409
        List<Notificacion> pendientes = notifRepo.findByUsuarioIdAndLeida(u.getId(), "N");
        boolean yaInvitado = pendientes.stream()
                .anyMatch(n -> "INVITACION_EQUIPO".equalsIgnoreCase(n.getTipo())
                        && equipoId.equals(n.getEquipoId()));
        if (yaInvitado) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Esta persona ya fue invitada al equipo");
        }

        // Crear token & mensaje (guardamos el token en el mensaje para extraerlo en el frontend)
        String raw = (u.getId() + ":" + equipoId + ":" + System.currentTimeMillis());
        String token = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));

        String msg = "Has sido invitado al equipo #" + equipoId
                + ". Para unirte usa el token: " + token;

        Notificacion n = new Notificacion();
        n.setUsuarioId(u.getId());
        n.setEquipoId(equipoId);
        n.setTitulo("Invitación a equipo");
        n.setMensaje(msg); // contiene el token
        n.setTipo("INVITACION_EQUIPO");
        n.setPrioridad("MEDIA");
        n.setLeida("N");
        n.setFechaEnvio(OffsetDateTime.now());

        notifRepo.save(n);

        // devolvemos 200 con el detalle de la notificación creada
        return ResponseEntity.ok(n);
    }

    @PutMapping("/{equipoId}/miembros/{usuarioId}/rol")
    public ResponseEntity<?> cambiarRol(@PathVariable Long equipoId,
                                        @PathVariable Long usuarioId,
                                        @RequestParam("rol") RolEquipo nuevoRol) {
        UsuarioEquipoId id = new UsuarioEquipoId(usuarioId, equipoId);
        Optional<UsuarioEquipo> ueOpt = ueRepo.findById(id);
        if (ueOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Miembro no encontrado");
        }
        UsuarioEquipo ue = ueOpt.get();
        ue.setRol(nuevoRol);
        return ResponseEntity.ok(ueRepo.save(ue));
    }

    @DeleteMapping("/{equipoId}/miembros/{usuarioId}")
    public ResponseEntity<?> remover(@PathVariable Long equipoId, @PathVariable Long usuarioId) {
        UsuarioEquipoId id = new UsuarioEquipoId(usuarioId, equipoId);
        if (!ueRepo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Miembro no existe");
        }
        ueRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
