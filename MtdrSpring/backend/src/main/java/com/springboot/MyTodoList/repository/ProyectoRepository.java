package com.springboot.MyTodoList.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.MyTodoList.model.Proyecto;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    List<Proyecto> findByCreadorId(Long creadorId);

    // Obtener el último proyecto (por id más alto)
    Proyecto findTopByOrderByIdDesc();

    // Proyectos por equipo
    List<Proyecto> findByEquipoId(Long equipoId);

    // ✅ Buscar por nombre (case-insensitive)
    Optional<Proyecto> findFirstByNombreProyectoIgnoreCase(String nombreProyecto);

    // ✅ NUEVO: helpers para "buscar por letras" (case-insensitive)
    List<Proyecto> findByNombreProyectoContainingIgnoreCase(String q);

    // ✅ NUEVO: versión limitada (si tu JPA soporta TopN)
    List<Proyecto> findTop10ByNombreProyectoContainingIgnoreCase(String q);
}




