package com.springboot.MyTodoList.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.springboot.MyTodoList.dto.TaskCreateDto;
import com.springboot.MyTodoList.dto.UpdateTaskDto;
import com.springboot.MyTodoList.model.*;
import com.springboot.MyTodoList.repository.*;
import com.springboot.MyTodoList.service.ChatGptManagerService;
import com.springboot.MyTodoList.service.TareaService;
import com.springboot.MyTodoList.service.EquipoService;

/**
 * Acciones del Bot Manager que orquesta TODOS los módulos del sistema.
 */
public class BotManagerActions {

    private static final Logger logger = LoggerFactory.getLogger(BotManagerActions.class);

    // Mapa para vincular chatId de Telegram con userId del sistema
    private static final Map<Long, Long> chatIdToUserId = new ConcurrentHashMap<>();

    private final TelegramLongPollingBot bot;
    private final ChatGptManagerService managerService;
    private final ChatbotRepository chatbotRepository;

    // Servicios
    private final TareaService tareaService;
    private final EquipoService equipoService;

    // Repositorios
    private final ProyectoRepository proyectoRepository;
    private final SprintRepository sprintRepository;
    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TareaRepository tareaRepository;
    private final UsuarioEquipoRepository usuarioEquipoRepository;

    private String requestText;
    private long chatId;

    public BotManagerActions(
            TelegramLongPollingBot bot,
            ChatGptManagerService managerService,
            ChatbotRepository chatbotRepository,
            TareaService tareaService,
            EquipoService equipoService,
            ProyectoRepository proyectoRepository,
            SprintRepository sprintRepository,
            EquipoRepository equipoRepository,
            UsuarioRepository usuarioRepository,
            TareaRepository tareaRepository,
            UsuarioEquipoRepository usuarioEquipoRepository) {
        this.bot = bot;
        this.managerService = managerService;
        this.chatbotRepository = chatbotRepository;
        this.tareaService = tareaService;
        this.equipoService = equipoService;
        this.proyectoRepository = proyectoRepository;
        this.sprintRepository = sprintRepository;
        this.equipoRepository = equipoRepository;
        this.usuarioRepository = usuarioRepository;
        this.tareaRepository = tareaRepository;
        this.usuarioEquipoRepository = usuarioEquipoRepository;
    }

    public void setRequestText(String cmd) { this.requestText = cmd; }
    public void setChatId(long id) { this.chatId = id; }

    public void handle() {
        ManagerIntent intent = managerService.interpret(chatId, requestText);
        String reply = processIntent(intent);
        send(reply);
        persistLog(reply);
    }

    private String processIntent(ManagerIntent intent) {
        if (intent == null) {
            return "No entendí tu mensaje, ¿puedes intentarlo de nuevo?";
        }

        logger.info("Procesando intent: module={}, action={}", intent.getModule(), intent.getAction());

        switch (intent.getModule()) {
            case GENERAL:
                return processGeneralIntent(intent);
            case TASK:
                return processTaskIntent(intent);
            case PROJECT:
                return processProjectIntent(intent);
            case TEAM:
                return processTeamIntent(intent);
            case SPRINT:
                return processSprintIntent(intent);
            case USER:
                return processUserIntent(intent);
            case KPI:
                return processKpiIntent(intent);
            default:
                return defaultReply(intent, "No reconozco ese módulo.");
        }
    }

    // ==================== GENERAL ====================
    private String processGeneralIntent(ManagerIntent intent) {
        switch (intent.getAction()) {
            case GREETING:
                return defaultReply(intent, getHelpMessage());
            case HELP:
                return getHelpMessage();
            case ASK_CLARIFICATION:
                return defaultReply(intent, "Necesito más información para ayudarte.");
            case UNKNOWN:
            default:
                return defaultReply(intent, "No estoy seguro de cómo ayudarte con eso, ¿puedes reformularlo?");
        }
    }

    private String getHelpMessage() {
        // Verificar si el usuario está identificado
        Long linkedUserId = chatIdToUserId.get(chatId);
        String identityStatus;

        if (linkedUserId != null) {
            Optional<Usuario> optUser = usuarioRepository.findById(linkedUserId);
            if (optUser.isPresent()) {
                identityStatus = "✅ Identificado como: *" + optUser.get().getNombre() + "*\n\n";
            } else {
                identityStatus = "⚠️ Usuario vinculado no encontrado. Identifícate de nuevo.\n\n";
            }
        } else {
            identityStatus = "👤 *Identifícate primero*: di 'soy [tu nombre]' (ej: 'soy Claudio')\n\n";
        }

        return "¡Hola! Soy tu asistente de productividad.\n\n" +
               identityStatus +
               "Puedo ayudarte con:\n\n" +
               "🔑 *IDENTIDAD*: 'soy Claudio', 'mi perfil', 'lista de usuarios'\n" +
               "📋 *TAREAS*: 'mis tareas', 'crea tarea X proyecto Y mañana', 'tareas de Pedro'\n" +
               "📁 *PROYECTOS*: 'mis proyectos', 'crea proyecto X', 'tareas del proyecto 3'\n" +
               "👥 *EQUIPOS*: 'mis equipos', 'crea equipo X', 'miembros del equipo 2'\n" +
               "🏃 *SPRINTS*: 'sprints del proyecto 3', 'crea sprint para proyecto 5'\n" +
               "📊 *MÉTRICAS*: 'mi productividad', 'KPIs de Juan', 'estadísticas'\n\n" +
               "¿En qué te puedo ayudar?";
    }

    // ==================== TAREAS ====================
    private String processTaskIntent(ManagerIntent intent) {
        switch (intent.getAction()) {
            case LIST_TASKS:
                return listTasks(intent);
            case CREATE_TASK:
                return createTask(intent);
            case GET_TASK:
                return getTask(intent);
            case UPDATE_TASK:
                return updateTask(intent);
            case MARK_DONE:
                return updateTaskStatus(intent, "done", "Tarea marcada como completada ✅");
            case MARK_PENDING:
                return updateTaskStatus(intent, "todo", "Tarea reabierta 🔄");
            case DELETE_TASK:
                return deleteTask(intent);
            default:
                return defaultReply(intent, "Acción de tarea no reconocida.");
        }
    }

    private String listTasks(ManagerIntent intent) {
        List<Tarea> tareas;

        // Determinar el ID del usuario vinculado
        Long linkedUserId = chatIdToUserId.get(chatId);
        String assigneeId = linkedUserId != null ? linkedUserId.toString() : Long.toString(chatId);

        if (intent.getProjectId() != null) {
            tareas = tareaRepository.findByProjectIdOrderByCreatedAtDesc(intent.getProjectId());
        } else if (intent.getSprintId() != null) {
            tareas = tareaRepository.findByAssigneeIdAndSprintIdOrderByCreatedAtDesc(
                    assigneeId, intent.getSprintId().toString());
        } else {
            tareas = tareaRepository.findByAssigneeId(assigneeId);
        }

        if (tareas.isEmpty()) {
            if (linkedUserId == null) {
                return "No encontré tareas asignadas a ti.\n\n" +
                       "Si tienes un usuario registrado, identifícate diciendo: 'soy [tu nombre]'\n" +
                       "Ejemplo: 'soy Claudio'";
            }
            return "No tienes tareas asignadas actualmente.";
        }

        // Obtener nombre del usuario si está vinculado
        String userName = "Tus";
        if (linkedUserId != null) {
            Optional<Usuario> optUser = usuarioRepository.findById(linkedUserId);
            if (optUser.isPresent()) {
                userName = "Tareas de " + optUser.get().getNombre();
            }
        }

        StringBuilder sb = new StringBuilder("📋 *" + userName + " tareas:*\n\n");
        int count = 0;
        for (Tarea t : tareas) {
            if (count >= 15) {
                sb.append("\n... y ").append(tareas.size() - 15).append(" tareas más.");
                break;
            }
            String statusEmoji = getStatusEmoji(t.getStatus());
            sb.append(statusEmoji).append(" #").append(t.getId())
              .append(" - ").append(t.getTitle())
              .append(" [").append(t.getStatus()).append("]\n");
            count++;
        }

        long pendientes = tareas.stream().filter(t -> !"done".equalsIgnoreCase(t.getStatus())).count();
        sb.append("\n📊 Total: ").append(tareas.size())
          .append(" | Pendientes: ").append(pendientes);

        return sb.toString();
    }

    private String createTask(ManagerIntent intent) {
        StringBuilder missing = new StringBuilder();
        if (!StringUtils.hasText(intent.getTaskTitle())) missing.append(" título,");
        if (intent.getProjectId() == null) missing.append(" projectId,");
        if (!StringUtils.hasText(intent.getDueDateIso())) missing.append(" fecha límite,");

        if (missing.length() > 0) {
            return "Para crear una tarea necesito:" + missing.substring(0, missing.length() - 1) +
                    ".\n\nEjemplo: 'crea tarea revisar código proyecto 1 para mañana'";
        }

        // Verificar que el proyecto existe
        Optional<Proyecto> optProyecto = proyectoRepository.findById(intent.getProjectId());
        if (optProyecto.isEmpty()) {
            return "No encontré el proyecto #" + intent.getProjectId() + ".\n" +
                   "Usa 'mis proyectos' para ver los proyectos disponibles.";
        }

        LocalDate dueDate = parseDate(intent.getDueDateIso());
        if (dueDate == null) {
            return "La fecha límite debe tener formato yyyy-MM-dd.";
        }

        // Usar usuario vinculado como assignee
        Long linkedUserId = chatIdToUserId.get(chatId);
        String assigneeId;
        String assigneeName;

        if (linkedUserId != null) {
            Optional<Usuario> optUser = usuarioRepository.findById(linkedUserId);
            if (optUser.isPresent()) {
                assigneeId = linkedUserId.toString();
                assigneeName = optUser.get().getNombre();
            } else {
                assigneeId = Long.toString(chatId);
                assigneeName = "Telegram user " + chatId;
            }
        } else {
            assigneeId = Long.toString(chatId);
            assigneeName = "Telegram user " + chatId;
        }

        TaskCreateDto dto = new TaskCreateDto();
        dto.title = intent.getTaskTitle();
        dto.description = StringUtils.hasText(intent.getDescription())
                ? intent.getDescription()
                : intent.getTaskTitle();
        dto.status = intent.getStatus() != null ? intent.getStatus() : "todo";
        dto.projectId = intent.getProjectId();
        dto.fechaLimite = dueDate;
        dto.assigneeId = assigneeId;
        dto.assigneeName = assigneeName;
        dto.priority = intent.getPriority() != null ? intent.getPriority() : "media";
        if (intent.getEstimatedHours() != null) {
            dto.estimatedHours = intent.getEstimatedHours();
        }

        try {
            Tarea createdTask = tareaService.createFromDto(dto);

            // Verificar en BD que la tarea fue creada
            Optional<Tarea> verification = tareaRepository.findById(createdTask.getId());
            if (verification.isPresent()) {
                Tarea t = verification.get();
                return String.format(
                    "✅ *Tarea creada exitosamente!*\n\n" +
                    "📋 ID: #%d\n" +
                    "📝 Título: %s\n" +
                    "📁 Proyecto: %s (#%d)\n" +
                    "👤 Asignada a: %s\n" +
                    "📅 Fecha límite: %s\n" +
                    "🏷️ Prioridad: %s\n" +
                    "📊 Estado: %s",
                    t.getId(),
                    t.getTitle(),
                    optProyecto.get().getNombreProyecto(),
                    t.getProjectId(),
                    assigneeName,
                    t.getFechaLimite() != null ? t.getFechaLimite().toString() : "N/A",
                    t.getPriority() != null ? t.getPriority() : "media",
                    t.getStatus()
                );
            } else {
                return "⚠️ La tarea fue procesada pero no pude verificarla en la base de datos.";
            }
        } catch (Exception ex) {
            logger.error("Error creando tarea", ex);
            return "❌ Error al crear la tarea: " + ex.getMessage();
        }
    }

    private String getTask(ManagerIntent intent) {
        if (intent.getTaskId() == null) {
            return "Necesito el ID de la tarea para mostrar sus detalles.";
        }
        Optional<Tarea> optTarea = tareaRepository.findById(intent.getTaskId());
        if (optTarea.isEmpty()) {
            return "No encontré la tarea #" + intent.getTaskId();
        }
        Tarea t = optTarea.get();
        return String.format(
            "📋 *Tarea #%d*\n" +
            "Título: %s\n" +
            "Estado: %s\n" +
            "Prioridad: %s\n" +
            "Proyecto: %d\n" +
            "Fecha límite: %s\n" +
            "Horas estimadas: %s\n" +
            "Horas reales: %s",
            t.getId(), t.getTitle(), t.getStatus(),
            t.getPriority() != null ? t.getPriority() : "N/A",
            t.getProjectId(),
            t.getFechaLimite() != null ? t.getFechaLimite().toString() : "N/A",
            t.getEstimatedHours() != null ? t.getEstimatedHours().toString() : "N/A",
            t.getRealHours() != null ? t.getRealHours().toString() : "N/A"
        );
    }

    private String updateTask(ManagerIntent intent) {
        if (intent.getTaskId() == null) {
            return "Necesito el ID de la tarea para actualizarla.";
        }
        try {
            UpdateTaskDto dto = new UpdateTaskDto();
            if (StringUtils.hasText(intent.getNewTitle())) {
                dto.title = intent.getNewTitle();
            }
            if (StringUtils.hasText(intent.getDescription())) {
                dto.description = intent.getDescription();
            }
            if (StringUtils.hasText(intent.getStatus())) {
                dto.status = intent.getStatus();
            }
            if (StringUtils.hasText(intent.getPriority())) {
                dto.priority = intent.getPriority();
            }
            tareaService.updateTask(intent.getTaskId(), dto);
            return defaultReply(intent, "✅ Tarea actualizada!");
        } catch (Exception ex) {
            logger.error("Error actualizando tarea", ex);
            return "No pude actualizar la tarea: " + ex.getMessage();
        }
    }

    private String updateTaskStatus(ManagerIntent intent, String status, String successMessage) {
        if (intent.getTaskId() == null) {
            return "Necesito el ID de la tarea para actualizarla.";
        }
        try {
            tareaService.updateStatus(intent.getTaskId(), status);
            return defaultReply(intent, successMessage);
        } catch (Exception ex) {
            logger.error("Error actualizando estado", ex);
            return "No pude actualizar esa tarea.";
        }
    }

    private String deleteTask(ManagerIntent intent) {
        if (intent.getTaskId() == null) {
            return "Necesito el ID de la tarea para eliminarla.";
        }

        // Verificar que la tarea existe antes de eliminar
        Optional<Tarea> optTarea = tareaRepository.findById(intent.getTaskId());
        if (optTarea.isEmpty()) {
            return "No encontré la tarea #" + intent.getTaskId() + ". Ya no existe o el ID es incorrecto.";
        }

        Tarea tareaAEliminar = optTarea.get();
        String titulo = tareaAEliminar.getTitle();

        try {
            tareaService.delete(intent.getTaskId());

            // Verificar que la tarea ya no existe
            Optional<Tarea> verification = tareaRepository.findById(intent.getTaskId());
            if (verification.isEmpty()) {
                return String.format(
                    "🗑️ *Tarea eliminada exitosamente!*\n\n" +
                    "📋 ID: #%d\n" +
                    "📝 Título: %s\n\n" +
                    "La tarea ha sido eliminada de la base de datos.",
                    intent.getTaskId(),
                    titulo
                );
            } else {
                return "⚠️ Hubo un problema: la tarea #" + intent.getTaskId() + " aún existe en la base de datos.";
            }
        } catch (Exception ex) {
            logger.error("Error eliminando tarea", ex);
            return "❌ No pude eliminar la tarea #" + intent.getTaskId() + ": " + ex.getMessage();
        }
    }

    // ==================== PROYECTOS ====================
    private String processProjectIntent(ManagerIntent intent) {
        switch (intent.getAction()) {
            case LIST_PROJECTS:
                return listProjects(intent);
            case CREATE_PROJECT:
                return createProject(intent);
            case GET_PROJECT:
                return getProject(intent);
            case GET_PROJECT_TASKS:
                return getProjectTasks(intent);
            default:
                return defaultReply(intent, "Acción de proyecto no reconocida.");
        }
    }

    private String listProjects(ManagerIntent intent) {
        List<Proyecto> proyectos;
        if (intent.getTeamId() != null) {
            proyectos = proyectoRepository.findByEquipoId(intent.getTeamId());
        } else {
            proyectos = proyectoRepository.findByCreadorId(chatId);
        }

        if (proyectos.isEmpty()) {
            // Intentar obtener todos si no hay del usuario
            proyectos = proyectoRepository.findAll();
            if (proyectos.isEmpty()) {
                return "No hay proyectos disponibles.";
            }
        }

        StringBuilder sb = new StringBuilder("📁 *Proyectos:*\n\n");
        for (Proyecto p : proyectos) {
            sb.append("• #").append(p.getId())
              .append(" - ").append(p.getNombreProyecto())
              .append(" [").append(p.getEstado() != null ? p.getEstado() : "activo").append("]\n");
        }
        return sb.toString();
    }

    private String createProject(ManagerIntent intent) {
        if (!StringUtils.hasText(intent.getProjectName())) {
            return "Necesito el nombre del proyecto para crearlo.";
        }

        Proyecto proyecto = new Proyecto();
        proyecto.setNombreProyecto(intent.getProjectName());
        proyecto.setDescripcion(intent.getDescription() != null ? intent.getDescription() : "");
        proyecto.setEstado("activo");
        proyecto.setCreadorId(chatId);
        proyecto.setEquipoId(intent.getTeamId());
        if (StringUtils.hasText(intent.getStartDateIso())) {
            proyecto.setFechaInicio(parseDate(intent.getStartDateIso()));
        }
        if (StringUtils.hasText(intent.getEndDateIso())) {
            proyecto.setFechaFin(parseDate(intent.getEndDateIso()));
        }

        proyectoRepository.save(proyecto);
        return defaultReply(intent, "✅ Proyecto '" + intent.getProjectName() + "' creado exitosamente!");
    }

    private String getProject(ManagerIntent intent) {
        Optional<Proyecto> optProyecto = Optional.empty();

        if (intent.getProjectId() != null) {
            optProyecto = proyectoRepository.findById(intent.getProjectId());
        } else if (StringUtils.hasText(intent.getProjectName())) {
            optProyecto = proyectoRepository.findFirstByNombreProyectoIgnoreCase(intent.getProjectName());
        }

        if (optProyecto.isEmpty()) {
            return "No encontré ese proyecto.";
        }

        Proyecto p = optProyecto.get();
        long totalTasks = tareaRepository.countByProjectId(p.getId());
        long completedTasks = tareaRepository.countCompletedByProjectId(p.getId());

        return String.format(
            "📁 *Proyecto #%d*\n" +
            "Nombre: %s\n" +
            "Estado: %s\n" +
            "Descripción: %s\n" +
            "Tareas: %d total, %d completadas\n" +
            "Fecha inicio: %s\n" +
            "Fecha fin: %s",
            p.getId(), p.getNombreProyecto(),
            p.getEstado() != null ? p.getEstado() : "activo",
            p.getDescripcion() != null ? p.getDescripcion() : "N/A",
            totalTasks, completedTasks,
            p.getFechaInicio() != null ? p.getFechaInicio().toString() : "N/A",
            p.getFechaFin() != null ? p.getFechaFin().toString() : "N/A"
        );
    }

    private String getProjectTasks(ManagerIntent intent) {
        if (intent.getProjectId() == null) {
            return "Necesito el ID del proyecto para mostrar sus tareas.";
        }
        List<Tarea> tareas = tareaRepository.findByProjectIdOrderByCreatedAtDesc(intent.getProjectId());
        if (tareas.isEmpty()) {
            return "No hay tareas en el proyecto #" + intent.getProjectId();
        }

        StringBuilder sb = new StringBuilder("📋 *Tareas del Proyecto #" + intent.getProjectId() + ":*\n\n");
        for (Tarea t : tareas) {
            String statusEmoji = getStatusEmoji(t.getStatus());
            sb.append(statusEmoji).append(" #").append(t.getId())
              .append(" - ").append(t.getTitle())
              .append(" [").append(t.getStatus()).append("]\n");
        }
        return sb.toString();
    }

    // ==================== EQUIPOS ====================
    private String processTeamIntent(ManagerIntent intent) {
        switch (intent.getAction()) {
            case LIST_TEAMS:
                return listTeams(intent);
            case CREATE_TEAM:
                return createTeam(intent);
            case GET_TEAM:
                return getTeam(intent);
            case GET_TEAM_MEMBERS:
                return getTeamMembers(intent);
            default:
                return defaultReply(intent, "Acción de equipo no reconocida.");
        }
    }

    private String listTeams(ManagerIntent intent) {
        List<Equipo> equipos = equipoRepository.findAll();
        if (equipos.isEmpty()) {
            return "No hay equipos disponibles.";
        }

        StringBuilder sb = new StringBuilder("👥 *Equipos:*\n\n");
        for (Equipo e : equipos) {
            sb.append("• #").append(e.getId())
              .append(" - ").append(e.getNombreEquipo()).append("\n");
        }
        return sb.toString();
    }

    private String createTeam(ManagerIntent intent) {
        if (!StringUtils.hasText(intent.getTeamName())) {
            return "Necesito el nombre del equipo para crearlo.";
        }

        Equipo equipo = new Equipo();
        equipo.setNombreEquipo(intent.getTeamName());
        equipo.setDescripcion(intent.getDescription() != null ? intent.getDescription() : "");

        equipoService.crearEquipoConAdmin(equipo, chatId);
        return defaultReply(intent, "✅ Equipo '" + intent.getTeamName() + "' creado!");
    }

    private String getTeam(ManagerIntent intent) {
        if (intent.getTeamId() == null) {
            return "Necesito el ID del equipo.";
        }
        Optional<Equipo> optEquipo = equipoRepository.findById(intent.getTeamId());
        if (optEquipo.isEmpty()) {
            return "No encontré el equipo #" + intent.getTeamId();
        }
        Equipo e = optEquipo.get();
        List<Proyecto> proyectos = proyectoRepository.findByEquipoId(e.getId());

        return String.format(
            "👥 *Equipo #%d*\n" +
            "Nombre: %s\n" +
            "Descripción: %s\n" +
            "Proyectos: %d",
            e.getId(), e.getNombreEquipo(),
            e.getDescripcion() != null ? e.getDescripcion() : "N/A",
            proyectos.size()
        );
    }

    private String getTeamMembers(ManagerIntent intent) {
        if (intent.getTeamId() == null) {
            return "Necesito el ID del equipo.";
        }
        List<UsuarioEquipo> members = usuarioEquipoRepository.findByEquipoId(intent.getTeamId());
        if (members.isEmpty()) {
            return "No hay miembros en el equipo #" + intent.getTeamId();
        }

        StringBuilder sb = new StringBuilder("👥 *Miembros del Equipo #" + intent.getTeamId() + ":*\n\n");
        for (UsuarioEquipo ue : members) {
            Optional<Usuario> optUser = usuarioRepository.findById(ue.getId().getUsuarioId());
            String userName = optUser.map(Usuario::getNombre).orElse("Usuario #" + ue.getId().getUsuarioId());
            sb.append("• ").append(userName)
              .append(" [").append(ue.getRol()).append("]\n");
        }
        return sb.toString();
    }

    // ==================== SPRINTS ====================
    private String processSprintIntent(ManagerIntent intent) {
        switch (intent.getAction()) {
            case LIST_SPRINTS:
                return listSprints(intent);
            case CREATE_SPRINT:
                return createSprint(intent);
            case GET_SPRINT:
                return getSprint(intent);
            case GET_SPRINT_TASKS:
                return getSprintTasks(intent);
            default:
                return defaultReply(intent, "Acción de sprint no reconocida.");
        }
    }

    private String listSprints(ManagerIntent intent) {
        if (intent.getProjectId() == null) {
            return "Necesito el ID del proyecto para listar sus sprints.";
        }
        List<Sprint> sprints = sprintRepository.findByProjectIdOrderByNumeroAsc(intent.getProjectId());
        if (sprints.isEmpty()) {
            return "No hay sprints en el proyecto #" + intent.getProjectId();
        }

        StringBuilder sb = new StringBuilder("🏃 *Sprints del Proyecto #" + intent.getProjectId() + ":*\n\n");
        for (Sprint s : sprints) {
            sb.append("• #").append(s.getId())
              .append(" - ").append(s.getTituloSprint())
              .append(" (Sprint ").append(s.getNumero()).append(")\n");
        }
        return sb.toString();
    }

    private String createSprint(ManagerIntent intent) {
        if (intent.getProjectId() == null) {
            return "Necesito el ID del proyecto para crear el sprint.";
        }
        if (!StringUtils.hasText(intent.getSprintTitle())) {
            return "Necesito el título del sprint.";
        }

        Sprint sprint = new Sprint();
        sprint.setProjectId(intent.getProjectId());
        sprint.setTituloSprint(intent.getSprintTitle());
        sprint.setDescripcionSprint(intent.getDescription() != null ? intent.getDescription() : "");

        if (StringUtils.hasText(intent.getStartDateIso())) {
            sprint.setFechaInicio(parseDate(intent.getStartDateIso()));
        }
        if (StringUtils.hasText(intent.getEndDateIso())) {
            sprint.setDuracion(parseDate(intent.getEndDateIso()));
        }

        // Calcular número del sprint
        List<Sprint> existingSprints = sprintRepository.findByProjectId(intent.getProjectId());
        sprint.setNumero(existingSprints.size() + 1);

        sprintRepository.save(sprint);
        return defaultReply(intent, "✅ Sprint '" + intent.getSprintTitle() + "' creado!");
    }

    private String getSprint(ManagerIntent intent) {
        if (intent.getSprintId() == null) {
            return "Necesito el ID del sprint.";
        }
        Optional<Sprint> optSprint = sprintRepository.findById(intent.getSprintId());
        if (optSprint.isEmpty()) {
            return "No encontré el sprint #" + intent.getSprintId();
        }
        Sprint s = optSprint.get();
        return String.format(
            "🏃 *Sprint #%d*\n" +
            "Título: %s\n" +
            "Número: %d\n" +
            "Proyecto: %d\n" +
            "Fecha inicio: %s\n" +
            "Fecha fin: %s",
            s.getId(), s.getTituloSprint(),
            s.getNumero() != null ? s.getNumero() : 0,
            s.getProjectId(),
            s.getFechaInicio() != null ? s.getFechaInicio().toString() : "N/A",
            s.getDuracion() != null ? s.getDuracion().toString() : "N/A"
        );
    }

    private String getSprintTasks(ManagerIntent intent) {
        if (intent.getSprintId() == null) {
            return "Necesito el ID del sprint.";
        }
        // Buscar tareas del sprint para el usuario actual
        List<Tarea> tareas = tareaRepository.findByAssigneeIdAndSprintIdOrderByCreatedAtDesc(
                Long.toString(chatId), intent.getSprintId().toString());

        if (tareas.isEmpty()) {
            return "No hay tareas en el sprint #" + intent.getSprintId();
        }

        StringBuilder sb = new StringBuilder("📋 *Tareas del Sprint #" + intent.getSprintId() + ":*\n\n");
        for (Tarea t : tareas) {
            String statusEmoji = getStatusEmoji(t.getStatus());
            sb.append(statusEmoji).append(" #").append(t.getId())
              .append(" - ").append(t.getTitle())
              .append(" [").append(t.getStatus()).append("]\n");
        }
        return sb.toString();
    }

    // ==================== USUARIOS ====================
    private String processUserIntent(ManagerIntent intent) {
        switch (intent.getAction()) {
            case LIST_USERS:
                return listUsers(intent);
            case GET_USER:
                return getUser(intent);
            case GET_MY_INFO:
                return getMyInfo(intent);
            case SET_MY_IDENTITY:
                return setMyIdentity(intent);
            case GET_USER_TASKS:
                return getUserTasks(intent);
            case GET_USER_KPI:
                return getUserKpi(intent);
            default:
                return defaultReply(intent, "Acción de usuario no reconocida.");
        }
    }

    private String listUsers(ManagerIntent intent) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) {
            return "No hay usuarios registrados.";
        }

        StringBuilder sb = new StringBuilder("👤 *Usuarios:*\n\n");
        for (Usuario u : usuarios) {
            sb.append("• #").append(u.getId())
              .append(" - ").append(u.getNombre())
              .append(" [").append(u.getRol() != null ? u.getRol() : "N/A").append("]\n");
        }
        return sb.toString();
    }

    private String getUser(ManagerIntent intent) {
        Optional<Usuario> optUser = Optional.empty();

        if (intent.getUserId() != null) {
            optUser = usuarioRepository.findById(intent.getUserId());
        } else if (StringUtils.hasText(intent.getUserName())) {
            // 1. Buscar por nombre exacto (case-insensitive)
            optUser = usuarioRepository.findByNombreIgnoreCase(intent.getUserName());

            // 2. Si no encuentra, buscar parcialmente
            if (optUser.isEmpty()) {
                List<Usuario> matches = usuarioRepository.findByNombreContainingIgnoreCase(intent.getUserName());
                if (!matches.isEmpty()) {
                    if (matches.size() == 1) {
                        optUser = Optional.of(matches.get(0));
                    } else {
                        // Múltiples coincidencias
                        StringBuilder sb = new StringBuilder("Encontré varios usuarios con ese nombre:\n\n");
                        for (Usuario u : matches) {
                            sb.append("• #").append(u.getId())
                              .append(" - ").append(u.getNombre())
                              .append(" [").append(u.getRol() != null ? u.getRol() : "N/A").append("]\n");
                        }
                        sb.append("\nPor favor, especifica el ID del usuario.");
                        return sb.toString();
                    }
                }
            }

            // 3. Buscar por email como último recurso
            if (optUser.isEmpty()) {
                optUser = usuarioRepository.findByEmailIgnoreCase(intent.getUserName());
            }
        }

        if (optUser.isEmpty()) {
            return "No encontré ningún usuario con el nombre '" + intent.getUserName() + "'. " +
                   "Usa 'lista de usuarios' para ver todos los usuarios disponibles.";
        }

        Usuario u = optUser.get();
        List<Tarea> tareas = tareaRepository.findByAssigneeId(u.getId().toString());
        long pendientes = tareas.stream().filter(t -> !"done".equalsIgnoreCase(t.getStatus())).count();
        long completadas = tareas.stream().filter(t -> "done".equalsIgnoreCase(t.getStatus())).count();

        return String.format(
            "👤 *Usuario #%d*\n" +
            "Nombre: %s\n" +
            "Email: %s\n" +
            "Rol: %s\n" +
            "Tareas totales: %d\n" +
            "✅ Completadas: %d\n" +
            "⏳ Pendientes: %d",
            u.getId(), u.getNombre(),
            u.getEmail() != null ? u.getEmail() : "N/A",
            u.getRol() != null ? u.getRol() : "N/A",
            tareas.size(), completadas, pendientes
        );
    }

    private String getMyInfo(ManagerIntent intent) {
        // Buscar usuario por chatId vinculado
        Long linkedUserId = chatIdToUserId.get(chatId);
        Optional<Usuario> optUser = Optional.empty();

        if (linkedUserId != null) {
            optUser = usuarioRepository.findById(linkedUserId);
        }

        if (optUser.isEmpty()) {
            return "No tienes un perfil de usuario vinculado.\n\n" +
                   "Puedes identificarte diciendo: 'soy [tu nombre]'\n" +
                   "Por ejemplo: 'soy Claudio' o 'me llamo Pedro'";
        }

        Usuario u = optUser.get();
        List<Tarea> tareas = tareaRepository.findByAssigneeId(u.getId().toString());
        long pendientes = tareas.stream().filter(t -> !"done".equalsIgnoreCase(t.getStatus())).count();
        long completadas = tareas.stream().filter(t -> "done".equalsIgnoreCase(t.getStatus())).count();

        return String.format(
            "👤 *Tu Perfil*\n" +
            "Nombre: %s\n" +
            "Email: %s\n" +
            "Rol: %s\n" +
            "Tareas totales: %d\n" +
            "✅ Completadas: %d\n" +
            "⏳ Pendientes: %d",
            u.getNombre(),
            u.getEmail() != null ? u.getEmail() : "N/A",
            u.getRol() != null ? u.getRol() : "N/A",
            tareas.size(), completadas, pendientes
        );
    }

    private String setMyIdentity(ManagerIntent intent) {
        if (!StringUtils.hasText(intent.getUserName())) {
            return "Necesito que me digas tu nombre. Por ejemplo: 'soy Claudio'";
        }

        // Buscar usuario por nombre
        Optional<Usuario> optUser = usuarioRepository.findByNombreIgnoreCase(intent.getUserName());

        if (optUser.isEmpty()) {
            List<Usuario> matches = usuarioRepository.findByNombreContainingIgnoreCase(intent.getUserName());
            if (matches.isEmpty()) {
                return "No encontré ningún usuario con el nombre '" + intent.getUserName() + "' en el sistema.\n" +
                       "Usa 'lista de usuarios' para ver los usuarios registrados.";
            } else if (matches.size() == 1) {
                optUser = Optional.of(matches.get(0));
            } else {
                StringBuilder sb = new StringBuilder("Encontré varios usuarios que coinciden:\n\n");
                for (Usuario u : matches) {
                    sb.append("• #").append(u.getId()).append(" - ").append(u.getNombre()).append("\n");
                }
                sb.append("\n¿Cuál eres tú? Dime 'soy [nombre completo]'");
                return sb.toString();
            }
        }

        Usuario user = optUser.get();
        chatIdToUserId.put(chatId, user.getId());

        logger.info("Usuario {} vinculado a chatId {}", user.getNombre(), chatId);

        return String.format(
            "✅ ¡Perfecto! Te he identificado como *%s* (ID: %d).\n\n" +
            "Ahora puedo mostrarte tus tareas, proyectos y métricas personalizadas.\n" +
            "Prueba diciendo: 'mis tareas' o 'mi productividad'",
            user.getNombre(), user.getId()
        );
    }

    private String getUserTasks(ManagerIntent intent) {
        Optional<Usuario> optUser = findUserByNameOrId(intent);

        if (optUser.isEmpty()) {
            return "No encontré ese usuario. Usa 'lista de usuarios' para ver los disponibles.";
        }

        Usuario user = optUser.get();
        List<Tarea> tareas = tareaRepository.findByAssigneeId(user.getId().toString());

        if (tareas.isEmpty()) {
            return "El usuario " + user.getNombre() + " no tiene tareas asignadas.";
        }

        StringBuilder sb = new StringBuilder("📋 *Tareas de " + user.getNombre() + ":*\n\n");
        int count = 0;
        for (Tarea t : tareas) {
            if (count >= 15) {
                sb.append("\n... y ").append(tareas.size() - 15).append(" tareas más.");
                break;
            }
            String statusEmoji = getStatusEmoji(t.getStatus());
            sb.append(statusEmoji).append(" #").append(t.getId())
              .append(" - ").append(t.getTitle())
              .append(" [").append(t.getStatus()).append("]\n");
            count++;
        }

        long pendientes = tareas.stream().filter(t -> !"done".equalsIgnoreCase(t.getStatus())).count();
        sb.append("\n📊 Total: ").append(tareas.size())
          .append(" | Pendientes: ").append(pendientes);

        return sb.toString();
    }

    private String getUserKpi(ManagerIntent intent) {
        Optional<Usuario> optUser = findUserByNameOrId(intent);

        if (optUser.isEmpty()) {
            return "No encontré ese usuario. Usa 'lista de usuarios' para ver los disponibles.";
        }

        Usuario user = optUser.get();
        List<Tarea> tareas = tareaRepository.findByAssigneeId(user.getId().toString());

        long total = tareas.size();
        long completed = tareas.stream().filter(t -> "done".equalsIgnoreCase(t.getStatus())).count();
        long pending = tareas.stream().filter(t -> "todo".equalsIgnoreCase(t.getStatus())).count();
        long inProgress = tareas.stream().filter(t -> "doing".equalsIgnoreCase(t.getStatus())).count();

        double totalEstimated = tareas.stream()
                .filter(t -> t.getEstimatedHours() != null)
                .mapToDouble(Tarea::getEstimatedHours)
                .sum();
        double totalReal = tareas.stream()
                .filter(t -> t.getRealHours() != null)
                .mapToDouble(Tarea::getRealHours)
                .sum();

        double completionRate = total > 0 ? (completed * 100.0 / total) : 0;
        double efficiency = totalEstimated > 0 ? (totalReal / totalEstimated * 100) : 0;

        return String.format(
            "📊 *KPIs de %s*\n\n" +
            "📋 Total tareas: %d\n" +
            "✅ Completadas: %d (%.1f%%)\n" +
            "🔄 En progreso: %d\n" +
            "⏳ Pendientes: %d\n" +
            "⏱️ Horas estimadas: %.1f\n" +
            "⏱️ Horas reales: %.1f\n" +
            "📈 Eficiencia: %.1f%%",
            user.getNombre(),
            total, completed, completionRate, inProgress, pending,
            totalEstimated, totalReal, efficiency
        );
    }

    private Optional<Usuario> findUserByNameOrId(ManagerIntent intent) {
        if (intent.getUserId() != null) {
            return usuarioRepository.findById(intent.getUserId());
        }

        if (StringUtils.hasText(intent.getUserName())) {
            Optional<Usuario> optUser = usuarioRepository.findByNombreIgnoreCase(intent.getUserName());
            if (optUser.isPresent()) {
                return optUser;
            }

            List<Usuario> matches = usuarioRepository.findByNombreContainingIgnoreCase(intent.getUserName());
            if (matches.size() == 1) {
                return Optional.of(matches.get(0));
            }
        }

        return Optional.empty();
    }

    // ==================== KPIs ====================
    private String processKpiIntent(ManagerIntent intent) {
        switch (intent.getAction()) {
            case GET_KPI_SUMMARY:
                return getKpiSummary(intent);
            case GET_PROJECT_KPI:
                return getProjectKpi(intent);
            case GET_SPRINT_KPI:
                return getSprintKpi(intent);
            case GET_PRODUCTIVITY:
                return getProductivity(intent);
            case GET_TASK_STATS:
                return getTaskStats(intent);
            case GET_BURNDOWN:
                return getBurndown(intent);
            case GET_VELOCITY:
                return getVelocity(intent);
            case GET_TEAM_ICL:
                return getTeamIcl(intent);
            case GET_TEAM_KPI:
                return getTeamKpi(intent);
            case GET_BURNOUT_USERS:
                return getBurnoutUsers(intent);
            case GET_DASHBOARD:
                return getDashboard(intent);
            case GET_SPRINT_HEALTH:
                return getSprintHealth(intent);
            default:
                return defaultReply(intent, "Acción de KPI no reconocida.");
        }
    }

    private String getKpiSummary(ManagerIntent intent) {
        long totalProjects = proyectoRepository.count();
        long totalTeams = equipoRepository.count();
        long totalUsers = usuarioRepository.count();
        long totalTasks = tareaRepository.count();
        Double avgHours = tareaRepository.avgRealHoursOfCompletedTasks();

        return String.format(
            "📊 *Resumen General de KPIs*\n\n" +
            "📁 Proyectos: %d\n" +
            "👥 Equipos: %d\n" +
            "👤 Usuarios: %d\n" +
            "📋 Tareas totales: %d\n" +
            "⏱️ Promedio horas/tarea completada: %.1f",
            totalProjects, totalTeams, totalUsers, totalTasks,
            avgHours != null ? avgHours : 0.0
        );
    }

    private String getProjectKpi(ManagerIntent intent) {
        if (intent.getProjectId() == null) {
            return "Necesito el ID del proyecto para sus KPIs.";
        }
        Optional<Proyecto> optProyecto = proyectoRepository.findById(intent.getProjectId());
        if (optProyecto.isEmpty()) {
            return "No encontré el proyecto #" + intent.getProjectId();
        }

        Proyecto p = optProyecto.get();
        long totalTasks = tareaRepository.countByProjectId(p.getId());
        long completedTasks = tareaRepository.countCompletedByProjectId(p.getId());
        long pendingTasks = totalTasks - completedTasks;
        double completionRate = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0;

        return String.format(
            "📊 *KPIs del Proyecto: %s*\n\n" +
            "📋 Tareas totales: %d\n" +
            "✅ Completadas: %d\n" +
            "⏳ Pendientes: %d\n" +
            "📈 Tasa de completado: %.1f%%",
            p.getNombreProyecto(), totalTasks, completedTasks, pendingTasks, completionRate
        );
    }

    private String getProductivity(ManagerIntent intent) {
        // Usar usuario vinculado
        Long linkedUserId = chatIdToUserId.get(chatId);
        String assigneeId = linkedUserId != null ? linkedUserId.toString() : Long.toString(chatId);

        List<Tarea> myTasks = tareaRepository.findByAssigneeId(assigneeId);

        if (myTasks.isEmpty() && linkedUserId == null) {
            return "No encontré tareas asignadas a ti.\n\n" +
                   "Identifícate primero diciendo: 'soy [tu nombre]'\n" +
                   "Ejemplo: 'soy Claudio'";
        }

        // Obtener nombre si está vinculado
        String userName = "Tu";
        if (linkedUserId != null) {
            Optional<Usuario> optUser = usuarioRepository.findById(linkedUserId);
            if (optUser.isPresent()) {
                userName = optUser.get().getNombre();
            }
        }

        long total = myTasks.size();
        long completed = myTasks.stream().filter(t -> "done".equalsIgnoreCase(t.getStatus())).count();
        long pending = myTasks.stream().filter(t -> "todo".equalsIgnoreCase(t.getStatus())).count();
        long inProgress = myTasks.stream().filter(t -> "doing".equalsIgnoreCase(t.getStatus())).count();

        double totalEstimated = myTasks.stream()
                .filter(t -> t.getEstimatedHours() != null)
                .mapToDouble(Tarea::getEstimatedHours)
                .sum();
        double totalReal = myTasks.stream()
                .filter(t -> t.getRealHours() != null)
                .mapToDouble(Tarea::getRealHours)
                .sum();

        return String.format(
            "📈 *Productividad de %s*\n\n" +
            "📋 Total tareas: %d\n" +
            "✅ Completadas: %d\n" +
            "🔄 En progreso: %d\n" +
            "⏳ Pendientes: %d\n" +
            "⏱️ Horas estimadas: %.1f\n" +
            "⏱️ Horas reales: %.1f\n" +
            "📊 Eficiencia: %.1f%%",
            userName, total, completed, inProgress, pending,
            totalEstimated, totalReal,
            totalEstimated > 0 ? (totalReal / totalEstimated * 100) : 0
        );
    }

    private String getTaskStats(ManagerIntent intent) {
        // Usar usuario vinculado
        Long linkedUserId = chatIdToUserId.get(chatId);
        String assigneeId = linkedUserId != null ? linkedUserId.toString() : Long.toString(chatId);

        List<Tarea> myTasks = tareaRepository.findByAssigneeId(assigneeId);

        if (myTasks.isEmpty() && linkedUserId == null) {
            return "No encontré tareas asignadas a ti.\n\n" +
                   "Identifícate primero diciendo: 'soy [tu nombre]'";
        }

        long total = myTasks.size();
        long completed = myTasks.stream().filter(t -> "done".equalsIgnoreCase(t.getStatus())).count();
        long pending = myTasks.stream().filter(t -> "todo".equalsIgnoreCase(t.getStatus())).count();
        long inProgress = myTasks.stream().filter(t -> "doing".equalsIgnoreCase(t.getStatus())).count();
        long highPriority = myTasks.stream()
                .filter(t -> "alta".equalsIgnoreCase(t.getPriority()))
                .count();

        return String.format(
            "📊 *Estadísticas de Tareas*\n\n" +
            "📋 Total: %d\n" +
            "✅ Completadas: %d (%.1f%%)\n" +
            "🔄 En progreso: %d\n" +
            "⏳ Pendientes: %d\n" +
            "🔴 Alta prioridad: %d",
            total, completed,
            total > 0 ? (completed * 100.0 / total) : 0,
            inProgress, pending, highPriority
        );
    }

    private String getSprintKpi(ManagerIntent intent) {
        if (intent.getSprintId() == null) {
            return "Necesito el ID del sprint para ver sus KPIs.";
        }

        Optional<Sprint> optSprint = sprintRepository.findById(intent.getSprintId());
        if (optSprint.isEmpty()) {
            return "No encontré el sprint #" + intent.getSprintId();
        }

        Sprint sprint = optSprint.get();
        String sprintIdStr = sprint.getId().toString();

        long totalTasks = tareaRepository.countBySprintId(sprintIdStr);
        long completedTasks = tareaRepository.countCompletedBySprintId(sprintIdStr);
        long inProgressTasks = tareaRepository.countInProgressBySprintId(sprintIdStr);
        long pendingTasks = totalTasks - completedTasks - inProgressTasks;

        Double estimatedHours = tareaRepository.sumEstimatedHoursBySprintId(sprintIdStr);
        Double realHours = tareaRepository.sumRealHoursBySprintId(sprintIdStr);

        double completionRate = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0;
        double efficiency = (estimatedHours != null && realHours != null && realHours > 0)
                ? (estimatedHours / realHours * 100) : 0;

        return String.format(
            "🏃 *KPIs del Sprint: %s*\n\n" +
            "📅 Número: %d\n" +
            "📁 Proyecto: #%d\n" +
            "📋 Tareas totales: %d\n" +
            "✅ Completadas: %d\n" +
            "🔄 En progreso: %d\n" +
            "⏳ Pendientes: %d\n" +
            "📈 Completado: %.1f%%\n" +
            "⏱️ Horas estimadas: %.1f\n" +
            "⏱️ Horas reales: %.1f\n" +
            "📊 Eficiencia: %.1f%%",
            sprint.getTituloSprint(),
            sprint.getNumero() != null ? sprint.getNumero() : 0,
            sprint.getProjectId(),
            totalTasks, completedTasks, inProgressTasks, pendingTasks,
            completionRate,
            estimatedHours != null ? estimatedHours : 0.0,
            realHours != null ? realHours : 0.0,
            efficiency
        );
    }

    private String getBurndown(ManagerIntent intent) {
        if (intent.getSprintId() == null) {
            return "Necesito el ID del sprint para mostrar el burndown.";
        }

        Optional<Sprint> optSprint = sprintRepository.findById(intent.getSprintId());
        if (optSprint.isEmpty()) {
            return "No encontré el sprint #" + intent.getSprintId();
        }

        Sprint sprint = optSprint.get();
        String sprintIdStr = sprint.getId().toString();

        long totalTasks = tareaRepository.countBySprintId(sprintIdStr);
        long completedTasks = tareaRepository.countCompletedBySprintId(sprintIdStr);
        long remainingTasks = totalTasks - completedTasks;

        LocalDate today = LocalDate.now();
        LocalDate fechaInicio = sprint.getFechaInicio();
        LocalDate fechaFin = sprint.getDuracion(); // duracion es fecha fin

        if (fechaInicio == null || fechaFin == null) {
            return "El sprint #" + intent.getSprintId() + " no tiene fechas configuradas.";
        }

        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        long daysElapsed = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, today);
        long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(today, fechaFin);

        double idealRemaining = totalTasks - ((double) totalTasks / totalDays * daysElapsed);
        double burndownGap = remainingTasks - idealRemaining;

        String status = today.isBefore(fechaInicio) ? "No iniciado" :
                       today.isAfter(fechaFin) ? "Finalizado" : "Activo";

        return String.format(
            "📉 *Burndown - Sprint: %s*\n\n" +
            "📊 Estado: %s\n" +
            "📋 Tareas restantes: %d\n" +
            "✅ Tareas completadas: %d\n" +
            "📈 Línea ideal restante: %.1f\n" +
            "⚠️ Gap: %.1f %s\n" +
            "📅 Días transcurridos: %d/%d\n" +
            "📅 Días restantes: %d\n\n" +
            "%s",
            sprint.getTituloSprint(),
            status,
            remainingTasks,
            completedTasks,
            idealRemaining,
            Math.abs(burndownGap),
            burndownGap > 0 ? "tareas por encima" : "tareas por debajo",
            daysElapsed, totalDays,
            daysRemaining > 0 ? daysRemaining : 0,
            burndownGap > 5 ? "⚠️ El sprint va retrasado" :
            burndownGap < -5 ? "✅ El sprint va adelantado" :
            "✓ El sprint va según lo planeado"
        );
    }

    private String getVelocity(ManagerIntent intent) {
        if (intent.getProjectId() == null) {
            return "Necesito el ID del proyecto para calcular la velocidad.";
        }

        Optional<Proyecto> optProyecto = proyectoRepository.findById(intent.getProjectId());
        if (optProyecto.isEmpty()) {
            return "No encontré el proyecto #" + intent.getProjectId();
        }

        Proyecto proyecto = optProyecto.get();
        List<Sprint> sprints = sprintRepository.findByProjectIdOrderByNumeroAsc(intent.getProjectId());

        if (sprints.isEmpty()) {
            return "El proyecto '" + proyecto.getNombreProyecto() + "' no tiene sprints registrados.";
        }

        StringBuilder sb = new StringBuilder(String.format(
            "🚀 *Velocidad - Proyecto: %s*\n\n", proyecto.getNombreProyecto()
        ));

        double totalVelocity = 0;
        int completedSprints = 0;

        for (Sprint sprint : sprints) {
            String sprintIdStr = sprint.getId().toString();
            long completed = tareaRepository.countCompletedBySprintId(sprintIdStr);

            LocalDate fechaFin = sprint.getDuracion();
            boolean isCompleted = fechaFin != null && LocalDate.now().isAfter(fechaFin);

            sb.append(String.format("Sprint %d: %d tareas %s\n",
                sprint.getNumero() != null ? sprint.getNumero() : 0,
                completed,
                isCompleted ? "✅" : "🔄"
            ));

            if (isCompleted) {
                totalVelocity += completed;
                completedSprints++;
            }
        }

        double avgVelocity = completedSprints > 0 ? totalVelocity / completedSprints : 0;

        sb.append(String.format(
            "\n📊 Total sprints: %d\n" +
            "✅ Sprints completados: %d\n" +
            "📈 Velocidad promedio: %.1f tareas/sprint",
            sprints.size(),
            completedSprints,
            avgVelocity
        ));

        return sb.toString();
    }

    private String getTeamIcl(ManagerIntent intent) {
        if (intent.getTeamId() == null) {
            return "Necesito el ID del equipo para calcular el ICL (Índice de Carga Laboral).";
        }

        Optional<Equipo> optEquipo = equipoRepository.findById(intent.getTeamId());
        if (optEquipo.isEmpty()) {
            return "No encontré el equipo #" + intent.getTeamId();
        }

        Equipo equipo = optEquipo.get();

        // Obtener todos los proyectos del equipo
        List<Proyecto> proyectos = proyectoRepository.findByEquipoId(intent.getTeamId());

        if (proyectos.isEmpty()) {
            return "El equipo '" + equipo.getNombreEquipo() + "' no tiene proyectos asignados.";
        }

        List<Long> projectIds = proyectos.stream().map(Proyecto::getId).collect(Collectors.toList());

        // Sumar horas estimadas y reales de todos los proyectos
        Double estimatedHours = tareaRepository.sumEstimatedHoursByProjectIdIn(projectIds);
        Double realHours = tareaRepository.sumRealHoursByProjectIdIn(projectIds);

        // Contar tareas activas y completadas
        long activeTasks = 0;
        long completedTasks = 0;
        for (Long projectId : projectIds) {
            long total = tareaRepository.countByProjectId(projectId);
            long completed = tareaRepository.countCompletedByProjectId(projectId);
            activeTasks += (total - completed);
            completedTasks += completed;
        }

        // Calcular ICL
        double hoursRatio = (estimatedHours != null && realHours != null && estimatedHours > 0)
                ? (realHours / estimatedHours) : 1.0;
        double tasksFactor = activeTasks / (double) (completedTasks + 1);
        double icl = hoursRatio * tasksFactor;

        String riskLevel;
        String message;
        if (icl <= 0.9) {
            riskLevel = "Bajo ✅";
            message = "Carga laboral saludable";
        } else if (icl <= 1.2) {
            riskLevel = "Medio ⚠️";
            message = "Carga sostenida cercana al límite";
        } else {
            riskLevel = "Alto 🔴";
            message = "Posible sobrecarga / riesgo de burnout";
        }

        return String.format(
            "📊 *ICL (Índice de Carga Laboral)*\n" +
            "👥 Equipo: %s\n\n" +
            "🎯 ICL: %.2f\n" +
            "⚠️ Nivel de riesgo: %s\n" +
            "📝 %s\n\n" +
            "📈 Métricas:\n" +
            "⏱️ Horas estimadas: %.1f\n" +
            "⏱️ Horas reales: %.1f\n" +
            "📋 Tareas activas: %d\n" +
            "✅ Tareas completadas: %d\n" +
            "📁 Proyectos: %d",
            equipo.getNombreEquipo(),
            icl,
            riskLevel,
            message,
            estimatedHours != null ? estimatedHours : 0.0,
            realHours != null ? realHours : 0.0,
            activeTasks,
            completedTasks,
            proyectos.size()
        );
    }

    private String getTeamKpi(ManagerIntent intent) {
        if (intent.getProjectId() == null) {
            return "Necesito el ID del proyecto para ver los KPIs del equipo.";
        }

        Optional<Proyecto> optProyecto = proyectoRepository.findById(intent.getProjectId());
        if (optProyecto.isEmpty()) {
            return "No encontré el proyecto #" + intent.getProjectId();
        }

        Proyecto proyecto = optProyecto.get();

        // Obtener todos los assignees únicos del proyecto
        List<String> assignees = tareaRepository.findDistinctAssigneeIdsByProjectId(intent.getProjectId());

        if (assignees.isEmpty()) {
            return "El proyecto '" + proyecto.getNombreProyecto() + "' no tiene tareas asignadas.";
        }

        StringBuilder sb = new StringBuilder(String.format(
            "👥 *KPIs del Equipo - Proyecto: %s*\n\n", proyecto.getNombreProyecto()
        ));

        for (String assigneeId : assignees) {
            long totalTasks = tareaRepository.countByAssigneeIdAndProjectId(assigneeId, intent.getProjectId());
            long completedTasks = tareaRepository.countCompletedByAssigneeIdAndProjectId(assigneeId, intent.getProjectId());
            long pendingTasks = totalTasks - completedTasks;

            double completionRate = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0;

            // Intentar obtener nombre del usuario
            String userName = assigneeId;
            try {
                Long userId = Long.parseLong(assigneeId);
                Optional<Usuario> optUser = usuarioRepository.findById(userId);
                if (optUser.isPresent()) {
                    userName = optUser.get().getNombre();
                }
            } catch (NumberFormatException e) {
                // assigneeId es email, buscar por email
                Optional<Usuario> optUser = usuarioRepository.findByEmailIgnoreCase(assigneeId);
                if (optUser.isPresent()) {
                    userName = optUser.get().getNombre();
                }
            }

            sb.append(String.format(
                "👤 %s\n" +
                "   📋 Total: %d | ✅ %d | ⏳ %d (%.1f%%)\n\n",
                userName,
                totalTasks, completedTasks, pendingTasks, completionRate
            ));
        }

        return sb.toString();
    }

    private String getBurnoutUsers(ManagerIntent intent) {
        if (intent.getProjectId() == null) {
            return "Necesito el ID del proyecto para identificar usuarios en riesgo.";
        }

        Optional<Proyecto> optProyecto = proyectoRepository.findById(intent.getProjectId());
        if (optProyecto.isEmpty()) {
            return "No encontré el proyecto #" + intent.getProjectId();
        }

        Proyecto proyecto = optProyecto.get();

        // Obtener todos los assignees del proyecto
        List<String> assignees = tareaRepository.findDistinctAssigneeIdsByProjectId(intent.getProjectId());

        if (assignees.isEmpty()) {
            return "El proyecto '" + proyecto.getNombreProyecto() + "' no tiene tareas asignadas.";
        }

        StringBuilder sb = new StringBuilder(String.format(
            "⚠️ *Análisis de Burnout - Proyecto: %s*\n\n", proyecto.getNombreProyecto()
        ));

        int burnoutCount = 0;
        final int BURNOUT_THRESHOLD = 3; // 3 o más tareas pendientes = riesgo

        for (String assigneeId : assignees) {
            List<Tarea> tasks = tareaRepository.findByAssigneeIdAndProjectIdOrderByCreatedAtDesc(
                assigneeId, intent.getProjectId()
            );

            long pending = tasks.stream()
                .filter(t -> !"done".equalsIgnoreCase(t.getStatus()))
                .count();

            Double realHours = tareaRepository.sumRealHoursByAssigneeId(assigneeId);
            Double estimatedHours = tareaRepository.sumEstimatedHoursByAssigneeId(assigneeId);

            double efficiency = (estimatedHours != null && realHours != null && estimatedHours > 0)
                    ? (estimatedHours / realHours * 100) : 100;

            // Determinar riesgo de burnout
            boolean hasRisk = pending >= BURNOUT_THRESHOLD || efficiency < 70;

            if (hasRisk) {
                // Obtener nombre del usuario
                String userName = assigneeId;
                try {
                    Long userId = Long.parseLong(assigneeId);
                    Optional<Usuario> optUser = usuarioRepository.findById(userId);
                    if (optUser.isPresent()) {
                        userName = optUser.get().getNombre();
                    }
                } catch (NumberFormatException e) {
                    Optional<Usuario> optUser = usuarioRepository.findByEmailIgnoreCase(assigneeId);
                    if (optUser.isPresent()) {
                        userName = optUser.get().getNombre();
                    }
                }

                String riskLevel = pending >= 5 ? "🔴 Alto" :
                                  pending >= BURNOUT_THRESHOLD ? "⚠️ Medio" : "⚠️ Bajo";

                sb.append(String.format(
                    "👤 %s - %s\n" +
                    "   📋 Tareas pendientes: %d\n" +
                    "   📊 Eficiencia: %.1f%%\n" +
                    "   %s\n\n",
                    userName,
                    riskLevel,
                    pending,
                    efficiency,
                    pending >= 5 ? "Sobrecarga crítica" :
                    pending >= BURNOUT_THRESHOLD ? "Carga elevada" :
                    "Eficiencia baja"
                ));

                burnoutCount++;
            }
        }

        if (burnoutCount == 0) {
            sb.append("✅ No se detectaron usuarios en riesgo de burnout.\n");
            sb.append("El equipo tiene una carga de trabajo saludable.");
        } else {
            sb.append(String.format(
                "📊 Resumen: %d de %d usuarios en riesgo",
                burnoutCount, assignees.size()
            ));
        }

        return sb.toString();
    }

    private String getDashboard(ManagerIntent intent) {
        if (intent.getProjectId() == null) {
            return "Necesito el ID del proyecto para mostrar el dashboard completo.";
        }

        Optional<Proyecto> optProyecto = proyectoRepository.findById(intent.getProjectId());
        if (optProyecto.isEmpty()) {
            return "No encontré el proyecto #" + intent.getProjectId();
        }

        Proyecto proyecto = optProyecto.get();

        // KPIs del proyecto
        long totalTasks = tareaRepository.countByProjectId(intent.getProjectId());
        long completedTasks = tareaRepository.countCompletedByProjectId(intent.getProjectId());
        double completionRate = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0;

        // Sprints
        List<Sprint> sprints = sprintRepository.findByProjectIdOrderByNumeroDesc(intent.getProjectId());
        int totalSprints = sprints.size();
        int activeSprints = 0;

        LocalDate today = LocalDate.now();
        for (Sprint sprint : sprints) {
            LocalDate inicio = sprint.getFechaInicio();
            LocalDate fin = sprint.getDuracion();
            if (inicio != null && fin != null &&
                !today.isBefore(inicio) && !today.isAfter(fin)) {
                activeSprints++;
            }
        }

        // Equipo
        List<String> assignees = tareaRepository.findDistinctAssigneeIdsByProjectId(intent.getProjectId());
        int teamSize = assignees.size();

        // Horas
        Double estimatedHours = tareaRepository.sumEstimatedHoursByProjectId(intent.getProjectId());
        Double realHours = tareaRepository.sumRealHoursByProjectId(intent.getProjectId());
        double efficiency = (estimatedHours != null && realHours != null && estimatedHours > 0)
                ? (estimatedHours / realHours * 100) : 0;

        return String.format(
            "📊 *Dashboard Completo*\n" +
            "📁 Proyecto: %s\n\n" +
            "=== TAREAS ===\n" +
            "📋 Total: %d\n" +
            "✅ Completadas: %d (%.1f%%)\n" +
            "⏳ Pendientes: %d\n\n" +
            "=== SPRINTS ===\n" +
            "🏃 Total: %d\n" +
            "🔄 Activos: %d\n\n" +
            "=== EQUIPO ===\n" +
            "👥 Miembros: %d\n\n" +
            "=== HORAS ===\n" +
            "⏱️ Estimadas: %.1f\n" +
            "⏱️ Reales: %.1f\n" +
            "📊 Eficiencia: %.1f%%\n\n" +
            "Usa comandos específicos para más detalles:\n" +
            "• 'burndown del sprint X'\n" +
            "• 'usuarios sobrecargados'\n" +
            "• 'velocidad del proyecto'\n" +
            "• 'KPIs del equipo'",
            proyecto.getNombreProyecto(),
            totalTasks, completedTasks, completionRate, totalTasks - completedTasks,
            totalSprints, activeSprints,
            teamSize,
            estimatedHours != null ? estimatedHours : 0.0,
            realHours != null ? realHours : 0.0,
            efficiency
        );
    }

    private String getSprintHealth(ManagerIntent intent) {
        if (intent.getSprintId() == null) {
            return "Necesito el ID del sprint para analizar su salud.";
        }

        Optional<Sprint> optSprint = sprintRepository.findById(intent.getSprintId());
        if (optSprint.isEmpty()) {
            return "No encontré el sprint #" + intent.getSprintId();
        }

        Sprint sprint = optSprint.get();
        String sprintIdStr = sprint.getId().toString();

        // Métricas básicas
        long totalTasks = tareaRepository.countBySprintId(sprintIdStr);
        long completedTasks = tareaRepository.countCompletedBySprintId(sprintIdStr);
        long inProgressTasks = tareaRepository.countInProgressBySprintId(sprintIdStr);

        double completionRate = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0;

        // Análisis temporal
        LocalDate today = LocalDate.now();
        LocalDate fechaInicio = sprint.getFechaInicio();
        LocalDate fechaFin = sprint.getDuracion();

        if (fechaInicio == null || fechaFin == null) {
            return "El sprint #" + intent.getSprintId() + " no tiene fechas configuradas.";
        }

        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        long daysElapsed = Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, today));
        long daysRemaining = Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(today, fechaFin));

        double timeProgress = totalDays > 0 ? (daysElapsed * 100.0 / totalDays) : 0;
        double gap = completionRate - timeProgress;

        // Determinar estado de salud
        String healthStatus;
        String healthIcon;
        String recommendation;

        if (gap >= 10) {
            healthStatus = "Excelente";
            healthIcon = "🟢";
            recommendation = "El sprint va adelantado. Buen ritmo de trabajo.";
        } else if (gap >= -5) {
            healthStatus = "Saludable";
            healthIcon = "🟢";
            recommendation = "El sprint va según lo planeado.";
        } else if (gap >= -15) {
            healthStatus = "En riesgo";
            healthIcon = "🟡";
            recommendation = "Considerar redistribuir tareas o extender plazos.";
        } else {
            healthStatus = "Crítico";
            healthIcon = "🔴";
            recommendation = "Requiere atención inmediata. Sprint en riesgo de no completarse.";
        }

        return String.format(
            "🏥 *Estado de Salud del Sprint*\n" +
            "🏃 Sprint: %s\n\n" +
            "%s Estado: %s\n\n" +
            "=== PROGRESO ===\n" +
            "📈 Tareas: %.1f%%\n" +
            "📅 Tiempo: %.1f%%\n" +
            "📊 Gap: %.1f%% %s\n\n" +
            "=== MÉTRICAS ===\n" +
            "📋 Total: %d tareas\n" +
            "✅ Completadas: %d\n" +
            "🔄 En progreso: %d\n" +
            "⏳ Pendientes: %d\n\n" +
            "=== TIEMPO ===\n" +
            "📅 Días transcurridos: %d/%d\n" +
            "📅 Días restantes: %d\n\n" +
            "💡 Recomendación: %s",
            sprint.getTituloSprint(),
            healthIcon, healthStatus,
            completionRate,
            timeProgress,
            Math.abs(gap), gap >= 0 ? "adelante" : "atrás",
            totalTasks, completedTasks, inProgressTasks, totalTasks - completedTasks - inProgressTasks,
            daysElapsed, totalDays, daysRemaining,
            recommendation
        );
    }

    // ==================== HELPERS ====================
    private LocalDate parseDate(String raw) {
        if (!StringUtils.hasText(raw)) return null;
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException ignored) {}
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d/M/uuuu", Locale.getDefault());
            return LocalDate.parse(raw, fmt);
        } catch (DateTimeParseException ex) {
            logger.warn("Fecha inválida: {}", raw);
            return null;
        }
    }

    private String getStatusEmoji(String status) {
        if (status == null) return "⚪";
        switch (status.toLowerCase()) {
            case "done":
            case "hecho":
                return "✅";
            case "doing":
            case "en progreso":
                return "🔄";
            case "todo":
            case "pendiente":
            default:
                return "⏳";
        }
    }

    private void send(String text) {
        SendMessage msg = new SendMessage(Long.toString(chatId), text);
        msg.enableMarkdown(true);
        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
            logger.error("Error enviando mensaje", e);
            // Intentar sin markdown si falla
            try {
                msg.enableMarkdown(false);
                msg.setText(text.replaceAll("\\*", ""));
                bot.execute(msg);
            } catch (TelegramApiException ex) {
                logger.error("Error enviando mensaje sin markdown", ex);
            }
        }
    }

    private void persistLog(String reply) {
        try {
            Chatbot log = new Chatbot();
            log.setUsuarioId(chatId);
            log.setMensajeUsuario(requestText);
            log.setRespuestaBot(reply);
            log.setFechaHora(OffsetDateTime.now());
            chatbotRepository.save(log);
        } catch (Exception ex) {
            logger.warn("No se pudo guardar el log del chatbot", ex);
        }
    }

    private String defaultReply(ManagerIntent intent, String fallback) {
        return StringUtils.hasText(intent != null ? intent.getBotReply() : null)
                ? intent.getBotReply()
                : fallback;
    }
}
