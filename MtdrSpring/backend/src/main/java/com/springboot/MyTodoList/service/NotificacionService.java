package com.springboot.MyTodoList.service;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.MyTodoList.model.Notificacion;
import com.springboot.MyTodoList.model.Proyecto;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.model.UsuarioEquipo;
import com.springboot.MyTodoList.repository.NotificacionRepository;
import com.springboot.MyTodoList.repository.ProyectoRepository;
import com.springboot.MyTodoList.repository.SprintRepository;
import com.springboot.MyTodoList.repository.UsuarioEquipoRepository;
import com.springboot.MyTodoList.repository.UsuarioRepository;

@Service
public class NotificacionService {

    private final NotificacionRepository repo;
    private final ProyectoRepository proyectoRepo;
    private final UsuarioRepository usuarioRepo;
    private final UsuarioEquipoRepository usuarioEquipoRepo;
    private final SprintRepository sprintRepo;

    public NotificacionService(
            NotificacionRepository repo,
            ProyectoRepository proyectoRepo,
            UsuarioRepository usuarioRepo,
            UsuarioEquipoRepository usuarioEquipoRepo,
            SprintRepository sprintRepo
    ) {
        this.repo = repo;
        this.proyectoRepo = proyectoRepo;
        this.usuarioRepo = usuarioRepo;
        this.usuarioEquipoRepo = usuarioEquipoRepo;
        this.sprintRepo = sprintRepo;
    }

    // ================== LISTAR / CREAR / LEER / BORRAR ==================

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

    // ================== HELPERS GENERALES ==================

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

    /**
     * Devuelve todos los IDs de usuarios que forman parte del equipo
     * del proyecto (tabla USUARIOS_EQUIPOS).
     */
    private List<Long> resolveMiembrosProyecto(Proyecto p) {
        if (p == null || p.getEquipoId() == null) {
            return List.of();
        }
        Long equipoId = p.getEquipoId();

        return usuarioEquipoRepo.findByEquipoId(equipoId).stream()
                .filter(Objects::nonNull)
                .map(UsuarioEquipo::getId)
                .filter(Objects::nonNull)
                .map(id -> id.getUsuarioId())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private Notificacion buildNotif(Long usuarioId,
                                    String titulo,
                                    String mensaje,
                                    String tipo,
                                    String prioridad,
                                    Long equipoId,
                                    Long creadaPor) {

        Notificacion n = new Notificacion();
        n.setUsuarioId(usuarioId);
        n.setTitulo(titulo);
        n.setMensaje(mensaje);
        n.setTipo(tipo);
        n.setPrioridad(prioridad);
        n.setFechaEnvio(OffsetDateTime.now());
        n.setLeida("N");
        n.setEquipoId(equipoId);
        n.setCreadaPor(creadaPor);
        return n;
    }

    // ================== TAREAS ==================

    /**
     * Notifica SOLO al asignado cuando se crea una tarea.
     */
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

        Notificacion n = buildNotif(
                destinatarioId,
                titulo,
                msg.toString(),
                "TAREAS",
                prioridad,
                p != null ? p.getEquipoId() : null,
                resolveCreadorId(ownerEmail)
        );

        repo.save(n);
    }

    /**
     * Notifica al asignado cuando se actualiza una tarea
     * (cambio de descripción, fecha, horas, estado, etc.).
     */
    @Transactional
    public void notificarTareaActualizada(Tarea tarea, String editorEmail) {
        if (tarea == null) return;

        Long destinatarioId = resolveUsuarioIdByEmail(tarea.getAssigneeId());
        if (destinatarioId == null) return;

        Proyecto p = null;
        if (tarea.getProjectId() != null) {
            p = proyectoRepo.findById(tarea.getProjectId()).orElse(null);
        }

        // Sprint (si la tarea pertenece a un sprint)
        Sprint sprint = null;
        if (tarea.getSprintId() != null) {
            try {
                Long sprintId = Long.valueOf(tarea.getSprintId());
                sprint = sprintRepo.findById(sprintId).orElse(null);
            } catch (NumberFormatException ignored) {}
        }

        StringBuilder titulo = new StringBuilder("Tarea actualizada");
        if (p != null && p.getNombreProyecto() != null) {
            titulo.append(" en ").append(p.getNombreProyecto());
        }

        StringBuilder msg = new StringBuilder();
        msg.append("La tarea \"")
           .append(tarea.getTitle() != null ? tarea.getTitle() : "sin título")
           .append("\" fue actualizada.");

        if (sprint != null && sprint.getTituloSprint() != null) {
            msg.append(" Sprint: \"").append(sprint.getTituloSprint()).append("\".");
        }

        if (tarea.getDescription() != null && !tarea.getDescription().isBlank()) {
            msg.append(" Descripción: \"")
               .append(tarea.getDescription())
               .append("\".");
        }

        if (tarea.getFechaLimite() != null) {
            msg.append(" Fecha límite: ").append(tarea.getFechaLimite()).append(".");
        }

        if (tarea.getStatus() != null) {
            msg.append(" Estado: ").append(tarea.getStatus()).append(".");
        }

        Notificacion n = buildNotif(
                destinatarioId,
                titulo.toString(),
                msg.toString(),
                "TAREAS",
                "Media",
                p != null ? p.getEquipoId() : null,
                resolveCreadorId(editorEmail)
        );

        repo.save(n);
    }

    // ================== PROYECTOS ==================

    @Transactional
    public void notificarProyectoCreado(Proyecto p, Long actorId) {
        if (p == null) return;

        List<Long> miembros = resolveMiembrosProyecto(p);
        if (miembros.isEmpty() && actorId != null) {
            miembros = List.of(actorId);
        }

        String nombre = (p.getNombreProyecto() != null) ? p.getNombreProyecto() : ("Proyecto #" + p.getId());
        String titulo = "Nuevo proyecto creado";
        String mensaje = "Se creó el proyecto \"" + nombre + "\".";

        for (Long uid : miembros) {
            Notificacion n = buildNotif(
                    uid,
                    titulo,
                    mensaje,
                    "PROYECTOS",
                    "Media",
                    p.getEquipoId(),
                    actorId
            );
            repo.save(n);
        }
    }

    @Transactional
    public void notificarProyectoActualizado(Proyecto p, Long actorId) {
        if (p == null) return;

        List<Long> miembros = resolveMiembrosProyecto(p);
        if (miembros.isEmpty() && actorId != null) {
            miembros = List.of(actorId);
        }

        String nombre = (p.getNombreProyecto() != null) ? p.getNombreProyecto() : ("Proyecto #" + p.getId());
        String titulo = "Proyecto actualizado";
        String mensaje = "El proyecto \"" + nombre + "\" fue actualizado.";

        for (Long uid : miembros) {
            Notificacion n = buildNotif(
                    uid,
                    titulo,
                    mensaje,
                    "PROYECTOS",
                    "Baja",
                    p.getEquipoId(),
                    actorId
            );
            repo.save(n);
        }
    }

    @Transactional
    public void notificarProyectoEliminado(Proyecto p, Long actorId) {
        if (p == null) return;

        List<Long> miembros = resolveMiembrosProyecto(p);
        if (miembros.isEmpty() && actorId != null) {
            miembros = List.of(actorId);
        }

        String nombre = (p.getNombreProyecto() != null) ? p.getNombreProyecto() : ("Proyecto #" + p.getId());
        String titulo = "Proyecto eliminado";
        String mensaje = "El proyecto \"" + nombre + "\" fue eliminado.";

        for (Long uid : miembros) {
            Notificacion n = buildNotif(
                    uid,
                    titulo,
                    mensaje,
                    "PROYECTOS",
                    "Alta",
                    p.getEquipoId(),
                    actorId
            );
            repo.save(n);
        }
    }

    // ================== SPRINTS ==================

    @Transactional
    public void notificarSprintCreado(Sprint s, Proyecto p, Long actorId) {
        if (s == null) return;
        if (p == null && s.getProjectId() != null) {
            p = proyectoRepo.findById(s.getProjectId()).orElse(null);
        }

        List<Long> miembros = resolveMiembrosProyecto(p);
        if (miembros.isEmpty() && actorId != null) {
            miembros = List.of(actorId);
        }

        String nombreProyecto = (p != null && p.getNombreProyecto() != null)
                ? p.getNombreProyecto()
                : (p != null ? ("Proyecto #" + p.getId()) : "proyecto");

        String titulo = "Nuevo sprint creado";
        String mensaje = "Se creó el sprint \"" +
                (s.getTituloSprint() != null ? s.getTituloSprint() : ("#" + s.getId())) +
                "\" en " + nombreProyecto + ".";

        for (Long uid : miembros) {
            Notificacion n = buildNotif(
                    uid,
                    titulo,
                    mensaje,
                    "SPRINTS",
                    "Media",
                    p != null ? p.getEquipoId() : null,
                    actorId
            );
            repo.save(n);
        }
    }

    @Transactional
    public void notificarSprintActualizado(Sprint s, Proyecto p, Long actorId) {
        if (s == null) return;
        if (p == null && s.getProjectId() != null) {
            p = proyectoRepo.findById(s.getProjectId()).orElse(null);
        }

        List<Long> miembros = resolveMiembrosProyecto(p);
        if (miembros.isEmpty() && actorId != null) {
            miembros = List.of(actorId);
        }

        String nombreProyecto = (p != null && p.getNombreProyecto() != null)
                ? p.getNombreProyecto()
                : (p != null ? ("Proyecto #" + p.getId()) : "proyecto");

        String titulo = "Sprint actualizado en " + nombreProyecto;

        StringBuilder msg = new StringBuilder();
        msg.append("El sprint \"")
           .append(s.getTituloSprint() != null ? s.getTituloSprint() : ("#" + s.getId()))
           .append("\" fue actualizado.");

        if (s.getDescripcionSprint() != null && !s.getDescripcionSprint().isBlank()) {
            msg.append(" Descripción: \"")
               .append(s.getDescripcionSprint())
               .append("\".");
        }

        if (s.getDuracion() != null) {
            msg.append(" Fecha fin: ").append(s.getDuracion()).append(".");
        }

        for (Long uid : miembros) {
            Notificacion n = buildNotif(
                    uid,
                    titulo,
                    msg.toString(),
                    "SPRINTS",
                    "Baja",
                    p != null ? p.getEquipoId() : null,
                    actorId
            );
            repo.save(n);
        }
    }

    @Transactional
    public void notificarSprintEliminado(Sprint s, Proyecto p, Long actorId) {
        if (s == null) return;
        if (p == null && s.getProjectId() != null) {
            p = proyectoRepo.findById(s.getProjectId()).orElse(null);
        }

        List<Long> miembros = resolveMiembrosProyecto(p);
        if (miembros.isEmpty() && actorId != null) {
            miembros = List.of(actorId);
        }

        String nombreProyecto = (p != null && p.getNombreProyecto() != null)
                ? p.getNombreProyecto()
                : (p != null ? ("Proyecto #" + p.getId()) : "proyecto");

        String titulo = "Sprint eliminado";
        String mensaje = "El sprint \"" +
                (s.getTituloSprint() != null ? s.getTituloSprint() : ("#" + s.getId())) +
                "\" en " + nombreProyecto + " fue eliminado.";

        for (Long uid : miembros) {
            Notificacion n = buildNotif(
                    uid,
                    titulo,
                    mensaje,
                    "SPRINTS",
                    "Alta",
                    p != null ? p.getEquipoId() : null,
                    actorId
            );
            repo.save(n);
        }
    }

    // ================== BORRADO DE NOTIFICACIONES ==================

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
