package com.springboot.MyTodoList.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.springboot.MyTodoList.model.Tarea;

public interface TareaRepository extends JpaRepository<Tarea, Long> {}

