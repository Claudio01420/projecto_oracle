package com.springboot.MyTodoList.controller;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.model.Notificacion;
import com.springboot.MyTodoList.model.RolEquipo;   // <-- usar OffsetDateTime
import com.springboot.MyTodoList.model.UsuarioEquipo;
import com.springboot.MyTodoList.model.UsuarioEquipoId;
import com.springboot.MyTodoList.repository.EquipoRepository;
import com.springboot.MyTodoList.repository.NotificacionRepository;
import com.springboot.MyTodoList.repository.UsuarioEquipoRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/equipos/invitaciones")
public class EquipoInvitacionController {

    private final NotificacionRepository notifRepo;
    private final UsuarioEquipoRepository ueRepo;
    private final EquipoRepository equipoRepo;

    public EquipoInvitacionController(NotificacionRepository notifRepo,
                                      UsuarioEquipoRepository ueRepo,
                                      EquipoRepository equipoRepo) {
        this.notifRepo = notifRepo;
        this.ueRepo = ueRepo;
        this.equipoRepo = equipoRepo;
    }

    /** Aceptar invitación por token. Crea la relación Usuario-Equipo si no existe. */
    @PostMapping("/{token}/aceptar")
    public ResponseEntity<?> aceptar(@PathVariable("token") String token) {
        Optional<Notificacion> opt = notifRepo
                .findTop1ByMensajeContainingIgnoreCaseOrderByFechaEnvioDesc(token);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Notificacion n = opt.get();
        if (n.getUsuarioId() == null || n.getEquipoId() == null) {
            return ResponseEntity.badRequest().body("Invitación inválida: faltan datos");
        }
        if (!equipoRepo.existsById(n.getEquipoId())) {
            return ResponseEntity.badRequest().body("El equipo ya no existe");
        }

        UsuarioEquipoId id = new UsuarioEquipoId(n.getUsuarioId(), n.getEquipoId());
        if (!ueRepo.existsById(id)) {
            UsuarioEquipo ue = new UsuarioEquipo(id, RolEquipo.USUARIO);
            ueRepo.save(ue);
        }

        // Marcar como leída con OffsetDateTime
        n.setLeida("S");
        n.setFechaLeido(OffsetDateTime.now());   // <-- aquí
        notifRepo.save(n);

        return ResponseEntity.ok("Te uniste al equipo #" + n.getEquipoId());
    }

    /** Rechazar invitación por token (solo marca como leída). */
    @PostMapping("/{token}/rechazar")
    public ResponseEntity<?> rechazar(@PathVariable("token") String token) {
        Optional<Notificacion> opt = notifRepo
                .findTop1ByMensajeContainingIgnoreCaseOrderByFechaEnvioDesc(token);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Notificacion n = opt.get();
        n.setLeida("S");
        n.setFechaLeido(OffsetDateTime.now());   // <-- y aquí
        notifRepo.save(n);

        return ResponseEntity.ok("Invitación rechazada");
    }
}
