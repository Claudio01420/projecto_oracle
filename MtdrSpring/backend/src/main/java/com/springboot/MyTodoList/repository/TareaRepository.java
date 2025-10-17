package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // Conteos por proyecto
    long countByProjectId(Long projectId);
    long countByProjectIdAndStatusIgnoreCase(Long projectId, String status);

    // Nuevo: contar completadas por proyecto (status='Hecho' OR completedAt IS NOT NULL)
    @Query("SELECT COUNT(t) FROM Tarea t WHERE t.projectId = :projectId AND (LOWER(t.status) = 'hecho' OR t.completedAt IS NOT NULL)")
    long countCompletedByProjectId(@Param("projectId") Long projectId);

    // Nuevos: operaciones sobre listas de projectIds para ICL
    @Query("SELECT COALESCE(SUM(t.estimatedHours),0) FROM Tarea t WHERE t.projectId IN :projectIds")
    Double sumEstimatedHoursByProjectIdIn(@Param("projectIds") List<Long> projectIds);

    @Query("SELECT COALESCE(SUM(t.realHours),0) FROM Tarea t WHERE t.projectId IN :projectIds")
    Double sumRealHoursByProjectIdIn(@Param("projectIds") List<Long> projectIds);

    long countByProjectIdInAndCompletedAtIsNull(List<Long> projectIds);

    long countByProjectIdInAndStatusIgnoreCase(List<Long> projectIds, String status);

    // Nuevo: promedio de horas reales para tareas completadas (status='Hecho' OR completedAt IS NOT NULL)
    @Query("SELECT COALESCE(AVG(t.realHours),0) FROM Tarea t WHERE LOWER(t.status) = 'hecho' OR t.completedAt IS NOT NULL")
    Double avgRealHoursOfCompletedTasks();

    // Nuevo: promedio de horas reales para todas las tareas (ignora realHours null)
    @Query("SELECT COALESCE(AVG(t.realHours),0) FROM Tarea t WHERE t.realHours IS NOT NULL")
    Double avgRealHoursAllTasks();
}
