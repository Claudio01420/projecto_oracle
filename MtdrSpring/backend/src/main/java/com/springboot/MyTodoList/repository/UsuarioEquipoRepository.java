package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.UsuarioEquipo;
import com.springboot.MyTodoList.model.UsuarioEquipoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioEquipoRepository extends JpaRepository<UsuarioEquipo, UsuarioEquipoId> {

    @Query("SELECT ue FROM UsuarioEquipo ue WHERE ue.id.equipoId = :equipoId")
    List<UsuarioEquipo> findByEquipoId(@Param("equipoId") Long equipoId);

    @Query("SELECT ue FROM UsuarioEquipo ue WHERE ue.id.usuarioId = :usuarioId")
    List<UsuarioEquipo> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("DELETE FROM UsuarioEquipo ue WHERE ue.id.equipoId = :equipoId")
    void deleteByEquipoId(@Param("equipoId") Long equipoId);
}
