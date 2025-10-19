package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    List<Proyecto> findByCreadorId(Long creadorId);

    // Obtener el último proyecto (por id más alto)
    Proyecto findTopByOrderByIdDesc();

    // Proyectos por equipo
    List<Proyecto> findByEquipoId(Long equipoId);

    // ✅ Nuevo: buscar por nombre (ignora mayúsculas/minúsculas)
    Optional<Proyecto> findFirstByNombreProyectoIgnoreCase(String nombreProyecto);
}

