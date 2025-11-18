package com.springboot.MyTodoList.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.MyTodoList.model.Tarea;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

    // EXISTENTES (dueño):
    List<Tarea> findByAssigneeId(String assigneeId);

    List<Tarea> findByAssigneeIdOrderByCreatedAtDesc(String assigneeId);

    Optional<Tarea> findByIdAndAssigneeId(Long id, String assigneeId);

    List<Tarea> findByAssigneeIdAndProjectIdOrderByCreatedAtDesc(String assigneeId, Long projectId);

    List<Tarea> findByAssigneeIdAndSprintIdOrderByCreatedAtDesc(String assigneeId, String sprintId);

    List<Tarea> findByAssigneeIdAndProjectIdAndSprintIdOrderByCreatedAtDesc(String assigneeId, Long projectId, String sprintId);

    // Tareas completadas de un sprint (todas)
    List<Tarea> findBySprintIdAndStatusIgnoreCaseOrderByCreatedAtDesc(String sprintId, String status);

    // Tareas completadas de un usuario en un sprint
    List<Tarea> findByAssigneeIdAndSprintIdAndStatusIgnoreCaseOrderByCreatedAtDesc(
            String assigneeId,
            String sprintId,
            String status
    );

    // ✅ NUEVOS (vista de equipo / compartida):
    List<Tarea> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<Tarea> findByProjectId(Long projectId);

    // KPIs/ICL (ya los usabas):
    long countByProjectId(Long projectId);

    long countByProjectIdAndStatusIgnoreCase(Long projectId, String status);

    @Query("SELECT COUNT(t) FROM Tarea t WHERE t.projectId = :projectId AND (LOWER(t.status) = 'hecho' OR t.completedAt IS NOT NULL)")
    long countCompletedByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT COALESCE(SUM(t.estimatedHours),0) FROM Tarea t WHERE t.projectId IN :projectIds")
    Double sumEstimatedHoursByProjectIdIn(@Param("projectIds") List<Long> projectIds);

    @Query("SELECT COALESCE(SUM(t.realHours),0) FROM Tarea t WHERE t.projectId IN :projectIds")
    Double sumRealHoursByProjectIdIn(@Param("projectIds") List<Long> projectIds);

    long countByProjectIdInAndCompletedAtIsNull(List<Long> projectIds);

    long countByProjectIdInAndStatusIgnoreCase(List<Long> projectIds, String status);

    @Query("SELECT COALESCE(AVG(t.realHours),0) FROM Tarea t WHERE LOWER(t.status) = 'hecho' OR t.completedAt IS NOT NULL")
    Double avgRealHoursOfCompletedTasks();

    @Query("SELECT COALESCE(AVG(t.realHours),0) FROM Tarea t WHERE t.realHours IS NOT NULL")
    Double avgRealHoursAllTasks();
}
