package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findByAssigneeId(String assigneeId);
    List<Tarea> findByAssigneeIdOrderByCreatedAtDesc(String assigneeId);
    Optional<Tarea> findByIdAndAssigneeId(Long id, String assigneeId);

    // Para la vista de proyectos: tareas de un dueño por proyecto
    List<Tarea> findByAssigneeIdAndProjectIdOrderByCreatedAtDesc(String assigneeId, Long projectId);

    // Alternativas por USER_EMAIL si alguna vez cambias a esa columna
    List<Tarea> findTop200ByUserEmailOrderByCreatedAtDesc(String userEmail);
    Optional<Tarea> findByIdAndUserEmail(Long id, String userEmail);
}
