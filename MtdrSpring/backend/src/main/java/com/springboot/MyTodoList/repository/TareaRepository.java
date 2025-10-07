package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    List<Tarea> findByAssigneeId(String assigneeId);
    List<Tarea> findByAssigneeIdOrderByCreatedAtDesc(String assigneeId); // NUEVO (si no existía)
}



