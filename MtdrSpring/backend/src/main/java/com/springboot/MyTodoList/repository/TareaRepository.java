package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

    // Listado por owner (email), ordenado por fecha creación desc
    List<Tarea> findByAssigneeIdOrderByCreatedAtDesc(String assigneeId);

    // Compat con código previo:
    List<Tarea> findByAssigneeId(String assigneeId);

    // Verificar propiedad antes de update/delete/complete
    Optional<Tarea> findByIdAndAssigneeId(Long id, String assigneeId);

    // Alternativas si usas USER_EMAIL en DB (fallback)
    List<Tarea> findTop200ByUserEmailOrderByCreatedAtDesc(String userEmail);
    Optional<Tarea> findByIdAndUserEmail(Long id, String userEmail);
}

