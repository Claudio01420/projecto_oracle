package com.springboot.MyTodoList.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.springboot.MyTodoList.dto.TaskCreateDto;
import com.springboot.MyTodoList.dto.UpdateTaskDto;
import com.springboot.MyTodoList.model.Chatbot;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.ChatbotRepository;
import com.springboot.MyTodoList.service.ChatGptTaskService;
import com.springboot.MyTodoList.service.TareaService;

public class BotActions {

    private static final Logger logger = LoggerFactory.getLogger(BotActions.class);

    private final TelegramLongPollingBot bot;
    private final TareaService tareaService;
    private final ChatGptTaskService chatGptTaskService;
    private final ChatbotRepository chatbotRepository;

    private String requestText;
    private long chatId;

    public BotActions(TelegramLongPollingBot bot,
                      TareaService tareaService,
                      ChatGptTaskService chatGptTaskService,
                      ChatbotRepository chatbotRepository) {
        this.bot = bot;
        this.tareaService = tareaService;
        this.chatGptTaskService = chatGptTaskService;
        this.chatbotRepository = chatbotRepository;
    }

    public void setRequestText(String cmd) { this.requestText = cmd; }
    public void setChatId(long id) { this.chatId = id; }

    public void handle() {
        BotIntent intent = chatGptTaskService.interpret(chatId, requestText);
        String reply = processIntent(intent);
        send(reply);
        persistLog(reply);
    }

    private String processIntent(BotIntent intent) {
        if (intent == null) {
            return "No entendí tu mensaje, ¿puedes intentarlo de nuevo?";
        }

        switch (intent.getAction()) {
            case GREETING:
            case HELP:
                return defaultReply(intent,
                        "Hola, puedo ayudarte a crear, listar y actualizar tus tareas. " +
                                "Pide por ejemplo: 'crea una tarea para el proyecto 10 mañana'.");
            case LIST_TASKS:
                return listTasks();
            case CREATE_TASK:
                return createTask(intent);
            case MARK_DONE:
                return updateStatus(intent, "done", "Tarea marcada como completada.");
            case MARK_PENDING:
                return updateStatus(intent, "todo", "Actualicé la tarea a pendiente.");
            case DELETE_TASK:
                return deleteTask(intent);
            case UPDATE_TITLE:
                return updateTitle(intent);
            case ASK_CLARIFICATION:
                return defaultReply(intent, "Necesito un poco más de contexto para ayudarte.");
            case UNKNOWN:
            default:
                return defaultReply(intent, "No estoy seguro de cómo ayudarte con eso, ¿puedes reformularlo?");
        }
    }

    private String listTasks() {
        List<Tarea> tareas = tareaService.listByAssignee(Long.toString(chatId));
        if (tareas.isEmpty()) {
            return "No encontré tareas asignadas a este chat.";
        }

        StringBuilder sb = new StringBuilder("Estas son tus tareas:\n");
        tareas.forEach(t -> sb.append("- #").append(t.getId())
                .append(" ").append(t.getTitle())
                .append(" [").append(t.getStatus()).append("]\n"));
        return sb.toString();
    }

    private String createTask(BotIntent intent) {
        StringBuilder missing = new StringBuilder();
        if (!StringUtils.hasText(intent.getTaskTitle())) missing.append(" título,");
        if (intent.getProjectId() == null) missing.append(" projectId,");
        if (!StringUtils.hasText(intent.getDueDateIso())) missing.append(" fecha límite,");

        if (missing.length() > 0) {
            return "Para crear una tarea necesito:" + missing.substring(0, missing.length() - 1) +
                    ". Inténtalo nuevamente incluyendo esos datos.";
        }

        LocalDate dueDate = parseDate(intent.getDueDateIso());
        if (dueDate == null) {
            return "La fecha límite debe tener formato yyyy-MM-dd. Ejemplo: 2024-12-01.";
        }

        TaskCreateDto dto = new TaskCreateDto();
        dto.title = intent.getTaskTitle();
        dto.description = StringUtils.hasText(intent.getDescription())
                ? intent.getDescription()
                : intent.getTaskTitle();
        dto.status = intent.getStatus() != null ? intent.getStatus() : "todo";
        dto.projectId = intent.getProjectId();
        dto.fechaLimite = dueDate;
        dto.assigneeId = Long.toString(chatId);
        dto.assigneeName = "Telegram user " + chatId;
        dto.priority = intent.getPriority();

        tareaService.createFromDto(dto);
        return defaultReply(intent, "Tarea creada ✅");
    }

    private String updateStatus(BotIntent intent, String status, String successMessage) {
        if (intent.getTaskId() == null) {
            return "Necesito el número de tarea para poder actualizarla.";
        }
        try {
            tareaService.updateStatus(intent.getTaskId(), status);
            return defaultReply(intent, successMessage);
        } catch (Exception ex) {
            logger.error("Error actualizando estado", ex);
            return "No pude actualizar esa tarea, verifica que el ID exista.";
        }
    }

    private String deleteTask(BotIntent intent) {
        if (intent.getTaskId() == null) {
            return "Necesito el ID de la tarea que quieres eliminar.";
        }
        try {
            tareaService.delete(intent.getTaskId());
            return defaultReply(intent, "Tarea eliminada.");
        } catch (Exception ex) {
            logger.error("Error eliminando tarea", ex);
            return "No pude eliminar esa tarea, verifica que exista.";
        }
    }

    private String updateTitle(BotIntent intent) {
        if (intent.getTaskId() == null || !StringUtils.hasText(intent.getNewTitle())) {
            return "Para editar necesito el ID y el nuevo título.";
        }
        try {
            UpdateTaskDto dto = new UpdateTaskDto();
            dto.title = intent.getNewTitle();
            tareaService.updateTask(intent.getTaskId(), dto);
            return defaultReply(intent, "Título actualizado.");
        } catch (Exception ex) {
            logger.error("Error actualizando título", ex);
            return "No pude editar esa tarea, ¿puedes intentar de nuevo?";
        }
    }

    private LocalDate parseDate(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException ignored) {
            // try dd/MM/yyyy
        }
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d/M/uuuu", Locale.getDefault());
            return LocalDate.parse(raw, fmt);
        } catch (DateTimeParseException ex) {
            logger.warn("Fecha inválida recibida del intent: {}", raw);
            return null;
        }
    }

    private void send(String text) {
        SendMessage msg = new SendMessage(Long.toString(chatId), text);
        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
            logger.error("Error enviando mensaje", e);
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

    private String defaultReply(BotIntent intent, String fallback) {
        return StringUtils.hasText(intent != null ? intent.getBotReply() : null)
                ? intent.getBotReply()
                : fallback;
    }
}
