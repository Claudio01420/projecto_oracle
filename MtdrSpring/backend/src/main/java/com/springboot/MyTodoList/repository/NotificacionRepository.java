package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    Optional<Notificacion> findTop1ByMensajeContainingIgnoreCaseOrderByFechaEnvioDesc(String token);

    List<Notificacion> findByUsuarioIdOrderByFechaEnvioDesc(Long usuarioId);

    List<Notificacion> findByUsuarioIdAndLeidaOrderByFechaEnvioDesc(Long usuarioId, String leida);

    // <- este era el que te faltaba para el service
    List<Notificacion> findByUsuarioIdAndLeida(Long usuarioId, String leida);

    long countByUsuarioIdAndLeida(Long usuarioId, String leida);

    Optional<Notificacion> findTopByUsuarioIdAndLeidaOrderByFechaEnvioDesc(Long usuarioId, String leida);
}
