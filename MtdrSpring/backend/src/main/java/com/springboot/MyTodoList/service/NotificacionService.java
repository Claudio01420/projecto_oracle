// NotificacionService.java  (REEMPLAZA COMPLETO)

package com.springboot.MyTodoList.service;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.MyTodoList.model.Notificacion;
import com.springboot.MyTodoList.model.Proyecto;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.NotificacionRepository;
import com.springboot.MyTodoList.repository.ProyectoRepository;
import com.springboot.MyTodoList.repository.UsuarioRepository;

@Service
public class NotificacionService {

    private final NotificacionRepository repo;
    private final ProyectoRepository proyectoRepo;
    private final UsuarioRepository usuarioRepo;

    public NotificacionService(
            NotificacionRepository repo,
            ProyectoRepository proyectoRepo,
            UsuarioRepository usuarioRepo
    ) {
        this.repo = repo;
        this.proyectoRepo = proyectoRepo;
        this.usuarioRepo = usuarioRepo;
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

    // ================== NUEVO: Notificar solo al ASIGNADO ==================

    private Long getUsuarioIdReflect(Object usuario) {
        if (usuario == null) return null;
        try {
            Method m = usuario.getClass().getMethod("getId");
            Object v = m.invoke(usuario);
            if (v instanceof Long) return (Long) v;
        } catch (Exception ignored) {}
        try {
            Method m = usuario.getClass().getMethod("getUsuarioId");
            Object v = m.invoke(usuario);
            if (v instanceof Long) return (Long) v;
        } catch (Exception ignored) {}
        return null;
    }

    private Long resolveUsuarioIdByEmail(String email) {
        if (email == null || email.isBlank()) return null;
        return usuarioRepo.findByEmailIgnoreCase(email.trim())
                .map(this::getUsuarioIdReflect)
                .orElse(null);
    }

    private Long resolveCreadorId(String ownerEmail) {
        return resolveUsuarioIdByEmail(ownerEmail);
    }

    @Transactional
    public void notificarNuevaTareaAAsignado(Long projectId, Tarea tarea, String ownerEmail) {
        if (tarea == null) return;

        final String assigneeEmail = tarea.getAssigneeId();
        Long destinatarioId = resolveUsuarioIdByEmail(assigneeEmail);
        if (destinatarioId == null) return;

        Proyecto p = null;
        if (projectId != null) {
            Optional<Proyecto> opt = proyectoRepo.findById(projectId);
            if (opt.isPresent()) p = opt.get();
        }

        String titulo = (p != null && p.getNombreProyecto() != null)
                ? "Nueva tarea en " + p.getNombreProyecto()
                : "Nueva tarea asignada";

        StringBuilder msg = new StringBuilder();
        msg.append("Se te asignó la tarea \"")
           .append(tarea.getTitle() != null ? tarea.getTitle() : "sin título")
           .append("\".");
        if (p != null) {
            msg.append(" Proyecto: ")
               .append(p.getNombreProyecto() != null ? p.getNombreProyecto() : ("#" + p.getId()))
               .append(".");
        }

        String prioridad = (tarea.getPriority() != null) ? tarea.getPriority() : "Media";

        Notificacion n = new Notificacion();
        n.setUsuarioId(destinatarioId);
        n.setTitulo(titulo);
        n.setMensaje(msg.toString());
        n.setTipo("TAREAS");
        n.setPrioridad(prioridad);
        n.setFechaEnvio(OffsetDateTime.now());
        n.setLeida("N");
        n.setEquipoId(p != null ? p.getEquipoId() : null);
        n.setCreadaPor(resolveCreadorId(ownerEmail));

        repo.save(n);
    }

    // ================== NUEVO: BORRADO ==================

    @Transactional
    public boolean borrar(Long notifId, Long usuarioId) {
        return repo.findById(notifId).map(n -> {
            if (n.getUsuarioId() != null && n.getUsuarioId().equals(usuarioId)) {
                repo.deleteById(notifId);
                return true;
            }
            return false;
        }).orElse(false);
    }

    @Transactional
    public long borrarTodas(Long usuarioId) {
        return repo.deleteByUsuarioId(usuarioId);
    }
}
