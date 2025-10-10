package com.springboot.MyTodoList.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.springboot.MyTodoList.config.BotProps;
import com.springboot.MyTodoList.service.TareaService;
import com.springboot.MyTodoList.util.BotActions;

@Component
public class ToDoItemBotController extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(ToDoItemBotController.class);

    private final BotProps botProps;
    private final TareaService tareaService;
    private final String telegramBotToken;

    public ToDoItemBotController(BotProps botProps,
                                 TareaService tareaService,
                                 @Value("${telegram.bot.token:}") String telegramBotToken) {
        this.botProps = botProps;
        this.tareaService = tareaService;
        this.telegramBotToken = telegramBotToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String messageTextFromTelegram = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        BotActions actions = new BotActions(this, tareaService);
        actions.setRequestText(messageTextFromTelegram);
        actions.setChatId(chatId);

        actions.fnStart();
        actions.fnListAll();
        actions.fnAddTask();
        actions.fnMarkDone();
        actions.fnMarkPending();
        actions.fnDeleteTask();
        actions.fnEditTask();
        actions.fnElse();
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