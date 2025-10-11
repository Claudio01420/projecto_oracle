package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Usuario;
import com.springboot.MyTodoList.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@CrossOrigin(origins = "*") // ajusta en producción
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository repo;

    public UsuarioController(UsuarioRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Usuario> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Usuario one(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @PostMapping
    public Usuario create(@RequestBody Usuario u) {
        return repo.save(u);
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
}
