package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioIdOrderByFechaEnvioDesc(Long usuarioId);

    List<Notificacion> findByUsuarioIdAndLeidaOrderByFechaEnvioDesc(Long usuarioId, String leida);

    long countByUsuarioIdAndLeida(Long usuarioId, String leida);

    // opcional por equipo (trazabilidad / filtros)
    List<Notificacion> findByUsuarioIdAndEquipoIdOrderByFechaEnvioDesc(Long usuarioId, Long equipoId);
}
