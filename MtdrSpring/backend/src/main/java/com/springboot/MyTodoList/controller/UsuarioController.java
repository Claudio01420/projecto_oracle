package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Usuario;
import com.springboot.MyTodoList.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@CrossOrigin(origins = "*") // habilitar para desarrollo local; afinar en producción
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository repo;
    public UsuarioController(UsuarioRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Usuario> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Usuario one(@PathVariable Long id) { return repo.findById(id).orElse(null); }

    @PostMapping
    public Usuario create(@RequestBody Usuario u) { return repo.save(u); }

    // nuevo endpoint para login: recibe { "email": "...", "contrasenia": "..." }
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Usuario credentials) {
        if (credentials.getEmail() == null || credentials.getContrasenia() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Optional<Usuario> opt = repo.findByEmail(credentials.getEmail());
        if (opt.isPresent() && credentials.getContrasenia().equals(opt.get().getContrasenia())) {
            Usuario user = opt.get();
            // evitar devolver la contraseña en la respuesta
            user.setContrasenia(null);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
