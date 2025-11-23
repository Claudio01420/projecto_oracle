package com.springboot.MyTodoList.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.springboot.MyTodoList.config.BotProps;
import com.springboot.MyTodoList.repository.*;
import com.springboot.MyTodoList.service.ChatGptManagerService;
import com.springboot.MyTodoList.service.TareaService;
import com.springboot.MyTodoList.service.EquipoService;
import com.springboot.MyTodoList.util.BotManagerActions;

/**
 * Bot de Telegram que actúa como Manager del sistema.
 * Orquesta todos los módulos: Tareas, Proyectos, Equipos, Sprints, Usuarios, KPIs.
 */
@Component
public class ToDoItemBotController extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(ToDoItemBotController.class);

    private final BotProps botProps;
    private final String telegramBotToken;

    // Manager Service (ChatGPT)
    private final ChatGptManagerService managerService;

    // Servicios
    private final TareaService tareaService;
    private final EquipoService equipoService;

    // Repositorios
    private final ChatbotRepository chatbotRepository;
    private final ProyectoRepository proyectoRepository;
    private final SprintRepository sprintRepository;
    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TareaRepository tareaRepository;
    private final UsuarioEquipoRepository usuarioEquipoRepository;

    public ToDoItemBotController(
            BotProps botProps,
            ChatGptManagerService managerService,
            TareaService tareaService,
            EquipoService equipoService,
            ChatbotRepository chatbotRepository,
            ProyectoRepository proyectoRepository,
            SprintRepository sprintRepository,
            EquipoRepository equipoRepository,
            UsuarioRepository usuarioRepository,
            TareaRepository tareaRepository,
            UsuarioEquipoRepository usuarioEquipoRepository,
            @Value("${telegram.bot.token:}") String telegramBotToken) {

        this.botProps = botProps;
        this.telegramBotToken = telegramBotToken;
        this.managerService = managerService;
        this.tareaService = tareaService;
        this.equipoService = equipoService;
        this.chatbotRepository = chatbotRepository;
        this.proyectoRepository = proyectoRepository;
        this.sprintRepository = sprintRepository;
        this.equipoRepository = equipoRepository;
        this.usuarioRepository = usuarioRepository;
        this.tareaRepository = tareaRepository;
        this.usuarioEquipoRepository = usuarioEquipoRepository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String messageTextFromTelegram = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        logger.info("Mensaje recibido de chatId {}: {}", chatId, messageTextFromTelegram);

        // Crear el Manager Actions con todos los repositorios
        BotManagerActions actions = new BotManagerActions(
            this,
            managerService,
            chatbotRepository,
            tareaService,
            equipoService,
            proyectoRepository,
            sprintRepository,
            equipoRepository,
            usuarioRepository,
            tareaRepository,
            usuarioEquipoRepository
        );

        actions.setRequestText(messageTextFromTelegram);
        actions.setChatId(chatId);
        actions.handle();
    }

    @Override
    public String getBotUsername() {
        return botProps.getName();
    }

    @Override
    public String getBotToken() {
        if (telegramBotToken != null && !telegramBotToken.trim().isEmpty()) {
            return telegramBotToken;
        }
        return botProps.getToken();
    }

    @Override
    public void onRegister() {
        logger.info("Bot Manager registrado correctamente - Orquestando todos los módulos");
    }

    @Override
    public void onClosing() {
        logger.info("Bot Manager cerrando sesión");
        super.onClosing();
    }
}
