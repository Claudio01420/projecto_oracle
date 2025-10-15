package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Búsqueda por email sin sensibilidad a mayúsc/minúsc
    Optional<Usuario> findByEmailIgnoreCase(String email);
}
