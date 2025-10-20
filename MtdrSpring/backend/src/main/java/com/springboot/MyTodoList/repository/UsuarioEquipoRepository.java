package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.UsuarioEquipo;
import com.springboot.MyTodoList.model.UsuarioEquipoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface UsuarioEquipoRepository extends JpaRepository<UsuarioEquipo, UsuarioEquipoId> {

    /* =========================
       LECTURAS (con @Query) 
       ========================= */
    @Query("SELECT ue FROM UsuarioEquipo ue WHERE ue.id.usuarioId = :usuarioId")
    List<UsuarioEquipo> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT ue FROM UsuarioEquipo ue WHERE ue.id.equipoId = :equipoId")
    List<UsuarioEquipo> findByEquipoId(@Param("equipoId") Long equipoId);

    /* =========================================
       BORRADOS por campo de la clave embebida
       ========================================= */

    // Opción A (derivado sobre propiedad anidada)
    @Modifying
    @Transactional
    void deleteById_EquipoId(Long equipoId);

    // Opción B (query explícita de respaldo)
    @Modifying
    @Transactional
    @Query("DELETE FROM UsuarioEquipo ue WHERE ue.id.equipoId = :equipoId")
    void deleteAllByEquipoId(@Param("equipoId") Long equipoId);
}
