package com.springboot.MyTodoList.util;

import com.springboot.MyTodoList.dto.TaskCreateDto;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.service.TareaService;
import com.springboot.MyTodoList.dto.UpdateTaskDto;

import org.springframework.web.server.ResponseStatusException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;

public class BotActions {

    private static final Logger logger = LoggerFactory.getLogger(BotActions.class);

    private String requestText;
    private long chatId;
    private boolean exit;
    private final TelegramLongPollingBot bot;
    private final TareaService tareaService;

    public BotActions(TelegramLongPollingBot bot, TareaService tareaService) {
        this.bot = bot;
        this.tareaService = tareaService;
        this.exit = false;
    }

    public void setRequestText(String cmd) { this.requestText = cmd; }
    public void setChatId(long id) { this.chatId = id; }

    /* ==== utilitario interno ==== */
    private void send(String text) {
        SendMessage msg = new SendMessage(Long.toString(chatId), text);
        try { bot.execute(msg); } catch (TelegramApiException e) { logger.error("Error enviando mensaje", e); }
    }

    /* ==== comandos principales ==== */
    public void fnStart() {
        if (exit || !requestText.equalsIgnoreCase("/start")) return;

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("📋 Ver tareas"));
        row.add(new KeyboardButton("i➕ Nueva tarea"));

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(row);

        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup.builder()
            .resizeKeyboard(true)
            .keyboard(keyboardRows)
            .build();

        SendMessage msg = new SendMessage(Long.toString(chatId),
                "👋 ¡Hola! Soy tu asistente de tareas. Elige una opción:");
        msg.setReplyMarkup(keyboard);

        try { bot.execute(msg); } catch (TelegramApiException e) { logger.error("Error al enviar start", e); }
        exit = true;
    }

    public void fnListAll() {
        if (exit || !(requestText.equalsIgnoreCase("/tareas") || requestText.equals("📋 Ver tareas"))) return;

        List<Tarea> tareas = tareaService.listByAssignee(Long.toString(chatId));
        if (tareas.isEmpty()) {
            send("No tienes tareas registradas.");
            exit = true;
            return;
        }

        StringBuilder sb = new StringBuilder("📋 *Tus tareas actuales:*\n\n");
        for (Tarea t : tareas) {
            sb.append("ID: ").append(t.getId())
              .append(" — ").append(t.getTitle())
              .append(" [").append(t.getStatus()).append("]\n");
        }

        send(sb.toString());
        exit = true;
    }

    public void fnAddTask() {
        if (exit || !(requestText.equalsIgnoreCase("/nueva") || requestText.equals("➕ Nueva tarea"))) return;
        send("Escribe el título de la nueva tarea:");
        exit = true;
    }

    public void fnMarkDone() {
        if (exit || !requestText.startsWith("/done")) return;
        try {
            Long id = Long.parseLong(requestText.replace("/done", "").trim());
            tareaService.updateStatus(id, "done");
            send("✅ Tarea marcada como completada.");
        } catch (ResponseStatusException e) {
            send("No se encontró la tarea con ese ID.");
        } catch (Exception e) {
            send("Error al procesar el comando /done. Usa /done [id]");
        }
        exit = true;
    }

    public void fnMarkPending() {
        if (exit || !requestText.startsWith("/pendiente")) return;
        try {
            Long id = Long.parseLong(requestText.replace("/pendiente", "").trim());
            tareaService.updateStatus(id, "todo");
            send("🔁 Tarea marcada como pendiente.");
        } catch (ResponseStatusException e) {
            send("No se encontró la tarea con ese ID.");
        } catch (Exception e) {
            send("Error al procesar el comando /pendiente. Usa /pendiente [id]");
        }
        exit = true;
    }

    public void fnDeleteTask() {
        if (exit || !requestText.startsWith("/delete")) return;
        try {
            Long id = Long.parseLong(requestText.replace("/delete", "").trim());
            tareaService.delete(id);
            send("🗑️ Tarea eliminada correctamente.");
        } catch (ResponseStatusException e) {
            send("No se encontró la tarea con ese ID.");
        } catch (Exception e) {
            send("Error al procesar el comando /delete. Usa /delete [id]");
        }
        exit = true;
    }
      public void fnEditTask() {
        if (exit) return;

        String trimmed = requestText.trim();
        if (!(trimmed.toLowerCase().startsWith("/editar") || trimmed.toLowerCase().startsWith("/edit"))) {
            return;
        }

        String[] parts = trimmed.split("\\s+", 3);
        if (parts.length < 3) {
            send("Usa /editar [id] [nuevo título]");
            exit = true;
            return;
        }

        try {
            Long id = Long.parseLong(parts[1]);
            UpdateTaskDto dto = new UpdateTaskDto();
            dto.title = parts[2];
            tareaService.updateTask(id, dto);
            send("✏️ Tarea actualizada.");
        } catch (NumberFormatException ex) {
            send("El ID debe ser numérico. Ejemplo: /editar 5 Nuevo título");
        } catch (ResponseStatusException ex) {
            send("No se encontró la tarea con ese ID.");
        } catch (Exception ex) {
            send("No fue posible actualizar la tarea.");
        }
        exit = true;
    }

    public void fnElse() {
        if (exit) return;
        if (requestText.startsWith("/")) return;

        TaskCreateDto dto = new TaskCreateDto();
        dto.title = requestText;
        dto.description = "Agregada desde Telegram";
        dto.status = "todo";
        dto.assigneeId = Long.toString(chatId);

        tareaService.createFromDto(dto);
        send("✅ Nueva tarea creada: " + requestText);
    }
}