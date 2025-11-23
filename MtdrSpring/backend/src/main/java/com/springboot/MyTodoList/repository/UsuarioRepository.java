package com.springboot.MyTodoList.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.MyTodoList.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);

    // Buscar por nombre (case-insensitive, parcial)
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por nombre exacto (case-insensitive)
    Optional<Usuario> findByNombreIgnoreCase(String nombre);

    // Buscar por teléfono (para vincular chatId de Telegram)
    Optional<Usuario> findByTelefono(Long telefono);

    // Buscar usuarios cuyo nombre contenga el texto
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Usuario> buscarPorNombre(@Param("nombre") String nombre);
}
