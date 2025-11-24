package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SprintRepository extends JpaRepository<Sprint, Long> {

    List<Sprint> findByProjectId(Long projectId);

    List<Sprint> findByProjectIdOrderByNumeroAsc(Long projectId);

    // ==================== MÉTODOS PARA KPIs ====================

    // Contar sprints de un proyecto
    long countByProjectId(Long projectId);

    // Sprints completados (fecha fin ya pasó)
    @Query("SELECT COUNT(s) FROM Sprint s WHERE s.projectId = :projectId AND s.duracion < :today")
    long countCompletedByProjectId(@Param("projectId") Long projectId, @Param("today") LocalDate today);

    // Sprint activo (fecha actual está entre inicio y fin)
    @Query("SELECT s FROM Sprint s WHERE s.projectId = :projectId AND s.fechaInicio <= :today AND s.duracion >= :today")
    List<Sprint> findActiveByProjectId(@Param("projectId") Long projectId, @Param("today") LocalDate today);

    // Sprints ordenados por número descendente (para velocidad histórica)
    List<Sprint> findByProjectIdOrderByNumeroDesc(Long projectId);
}
