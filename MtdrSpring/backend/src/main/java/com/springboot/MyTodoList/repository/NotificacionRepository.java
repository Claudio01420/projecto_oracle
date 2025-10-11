package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // Listado básico por usuario (todas)
    List<Notificacion> findByUsuarioIdOrderByFechaEnvioDesc(Long usuarioId);

    // Por usuario + leído/no leído
    List<Notificacion> findByUsuarioIdAndLeidaOrderByFechaEnvioDesc(Long usuarioId, String leida);

    // Por usuario + tipos
    List<Notificacion> findByUsuarioIdAndTipoInOrderByFechaEnvioDesc(Long usuarioId, Collection<String> tipos);

    // Por usuario + leído/no leído + tipos
    List<Notificacion> findByUsuarioIdAndLeidaAndTipoInOrderByFechaEnvioDesc(Long usuarioId, String leida, Collection<String> tipos);
}
