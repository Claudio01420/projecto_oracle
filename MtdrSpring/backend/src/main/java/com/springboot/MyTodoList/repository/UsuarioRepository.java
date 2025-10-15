package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // nuevo: permite buscar usuario por email para el login
    Optional<Usuario> findByEmail(String email);

     // Derivación estándar de Spring Data (necesita que el campo en la entidad se llame "email")
    Optional<Usuario> findByEmailIgnoreCase(String email);

    // Extra robusto (funciona aunque la BD sea case sensitive)
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<Usuario> findByEmailCi(String email);
}
