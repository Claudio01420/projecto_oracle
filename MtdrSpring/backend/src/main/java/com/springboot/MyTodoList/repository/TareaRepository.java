package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    // Por si lo usas en algún otro lado
    List<Tarea> findByAssigneeId(String assigneeId);

    // Necesario para listarlas ordenadas por fecha de creación (DESC)
    List<Tarea> findByAssigneeIdOrderByCreatedAtDesc(String assigneeId);
}


