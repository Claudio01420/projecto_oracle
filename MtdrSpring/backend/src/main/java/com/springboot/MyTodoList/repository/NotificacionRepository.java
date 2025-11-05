// NotificacionRepository.java  (REEMPLAZA COMPLETO)

package com.springboot.MyTodoList.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.MyTodoList.model.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    Optional<Notificacion> findTop1ByMensajeContainingIgnoreCaseOrderByFechaEnvioDesc(String token);

    List<Notificacion> findByUsuarioIdOrderByFechaEnvioDesc(Long usuarioId);

    List<Notificacion> findByUsuarioIdAndLeidaOrderByFechaEnvioDesc(Long usuarioId, String leida);

    List<Notificacion> findByUsuarioIdAndLeida(Long usuarioId, String leida);

    long countByUsuarioIdAndLeida(Long usuarioId, String leida);

    Optional<Notificacion> findTopByUsuarioIdAndLeidaOrderByFechaEnvioDesc(Long usuarioId, String leida);

    // ==== NUEVO: helpers para borrar ====
    void deleteByIdAndUsuarioId(Long id, Long usuarioId);

    long deleteByUsuarioId(Long usuarioId);
}
