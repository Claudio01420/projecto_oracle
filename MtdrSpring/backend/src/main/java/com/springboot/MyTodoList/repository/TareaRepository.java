package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findByAssigneeId(String assigneeId);
    List<Tarea> findByAssigneeIdOrderByCreatedAtDesc(String assigneeId);

    Optional<Tarea> findByIdAndAssigneeId(Long id, String assigneeId);

    // Filtros
    List<Tarea> findByAssigneeIdAndProjectIdOrderByCreatedAtDesc(String assigneeId, Long projectId);
    List<Tarea> findByAssigneeIdAndSprintIdOrderByCreatedAtDesc(String assigneeId, String sprintId);
    List<Tarea> findByAssigneeIdAndProjectIdAndSprintIdOrderByCreatedAtDesc(String assigneeId, Long projectId, String sprintId);

    // Si alguna vez usas USER_EMAIL:
    List<Tarea> findTop200ByUserEmailOrderByCreatedAtDesc(String userEmail);
    Optional<Tarea> findByIdAndUserEmail(Long id, String userEmail);
}
