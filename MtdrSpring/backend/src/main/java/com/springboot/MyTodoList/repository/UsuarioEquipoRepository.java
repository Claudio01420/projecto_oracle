package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.UsuarioEquipo;
import com.springboot.MyTodoList.model.UsuarioEquipoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioEquipoRepository extends JpaRepository<UsuarioEquipo, UsuarioEquipoId> {}
