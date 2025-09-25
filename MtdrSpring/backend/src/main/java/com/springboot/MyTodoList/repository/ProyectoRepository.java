package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {}
