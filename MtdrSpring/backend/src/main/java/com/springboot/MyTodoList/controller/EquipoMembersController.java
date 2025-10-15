package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.RolEquipo;
import com.springboot.MyTodoList.model.UsuarioEquipo;
import com.springboot.MyTodoList.model.UsuarioEquipoId;
import com.springboot.MyTodoList.repository.EquipoRepository;
import com.springboot.MyTodoList.repository.UsuarioEquipoRepository;
import com.springboot.MyTodoList.repository.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*") // <-- corregido
@RestController
@RequestMapping("/equipos")
public class EquipoMembersController {

    private final UsuarioRepository usuarioRepo;
    private final UsuarioEquipoRepository ueRepo;
    private final EquipoRepository equipoRepo;

    public EquipoMembersController(
            UsuarioRepository usuarioRepo,
            UsuarioEquipoRepository ueRepo,
            EquipoRepository equipoRepo) {
        this.usuarioRepo = usuarioRepo;
        this.ueRepo = ueRepo;
        this.equipoRepo = equipoRepo;
    }

    // Listar miembros de un equipo
    @GetMapping("/{equipoId}/miembros")
    public List<UsuarioEquipo> miembros(@PathVariable Long equipoId) {
        return ueRepo.findByEquipoId(equipoId);
    }

    // Invitar por correo (crea relación con rol)
    @PostMapping("/{equipoId}/invitar")
    public ResponseEntity<?> invitar(@PathVariable Long equipoId,
                                     @RequestParam("email") String email,
                                     @RequestParam("rol") RolEquipo rol) {

        // 1) Verifica que el equipo exista
        if (!equipoRepo.existsById(equipoId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Equipo no existe");
        }

        // 2) Busca el usuario por email (usa findByEmailIgnoreCase de tu repo)
        return usuarioRepo.findByEmailIgnoreCase(email)
                .map(u -> {
                    UsuarioEquipoId id = new UsuarioEquipoId(u.getId(), equipoId);

                    // Evitar duplicados sin excepción
                    if (ueRepo.existsById(id)) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya es miembro del equipo");
                    }

                    try {
                        UsuarioEquipo ue = new UsuarioEquipo(id, rol);
                        UsuarioEquipo saved = ueRepo.save(ue);
                        return ResponseEntity.ok(saved);
                    } catch (DataIntegrityViolationException e) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya está en el equipo");
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado"));
    }

    // Cambiar rol de un miembro
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

    // Remover miembro
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
