package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {}
