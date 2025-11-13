package com.springboot.MyTodoList.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.springboot.MyTodoList.config.BotProps;
import com.springboot.MyTodoList.repository.ChatbotRepository;
import com.springboot.MyTodoList.service.ChatGptTaskService;
import com.springboot.MyTodoList.service.TareaService;
import com.springboot.MyTodoList.util.BotActions;

@Component
public class ToDoItemBotController extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(ToDoItemBotController.class);

    private final BotProps botProps;
    private final TareaService tareaService;
    private final String telegramBotToken;
    private final ChatGptTaskService chatGptTaskService;
    private final ChatbotRepository chatbotRepository;

    public ToDoItemBotController(BotProps botProps,
                                 TareaService tareaService,
                                 ChatGptTaskService chatGptTaskService,
                                 ChatbotRepository chatbotRepository,
                                 @Value("${telegram.bot.token:}") String telegramBotToken) {
        this.botProps = botProps;
        this.tareaService = tareaService;
        this.telegramBotToken = telegramBotToken;
        this.chatGptTaskService = chatGptTaskService;
        this.chatbotRepository = chatbotRepository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String messageTextFromTelegram = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        BotActions actions = new BotActions(this, tareaService, chatGptTaskService, chatbotRepository);
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
        logger.info("Bot registrado correctamente");
    }

    @Override
    public void onClosing() {
        logger.info("Bot cerrando sesión");
        super.onClosing();
    }
}
