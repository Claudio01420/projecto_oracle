package com.springboot.MyTodoList.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.MyTodoList.model.Notificacion;
import com.springboot.MyTodoList.repository.NotificacionRepository;

@Service
public class NotificacionService {

    private final NotificacionRepository repo;

    public NotificacionService(NotificacionRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Notificacion> listarPorUsuario(Long usuarioId) {
        return repo.findByUsuarioIdOrderByFechaEnvioDesc(usuarioId);
    }

    @Transactional
    public Notificacion crear(Notificacion n) {
        if (n.getFechaEnvio() == null) n.setFechaEnvio(OffsetDateTime.now());
        if (n.getLeida() == null) n.setLeida("N");
        return repo.save(n);
    }

    @Transactional
    public void marcarLeida(Long id) {
        repo.findById(id).ifPresent(n -> {
            n.setLeida("S");
            n.setFechaLeido(OffsetDateTime.now());
            repo.save(n);
        });
    }

    @Transactional
    public void marcarTodasLeidas(Long usuarioId) {
        List<Notificacion> list = repo.findByUsuarioIdAndLeida(usuarioId, "N");
        OffsetDateTime now = OffsetDateTime.now();
        for (Notificacion n : list) {
            n.setLeida("S");
            n.setFechaLeido(now);
        }
        repo.saveAll(list);
    }
}

