package com.springboot.MyTodoList.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.springboot.MyTodoList.controller.ToDoItemBotController;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@ConditionalOnProperty(name = "DISABLE_TELEGRAM_BOT", havingValue = "false", matchIfMissing = true)
public class TelegramBotConfig {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotConfig.class);

    private final ToDoItemBotController bot;

    public TelegramBotConfig(ToDoItemBotController bot) {
        this.bot = bot;
    }

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            log.info("Bot de Telegram registrado correctamente.");
        } catch (TelegramApiException ex) {
            log.warn("No se pudo registrar el bot en Telegram: {}", ex.getMessage(), ex);
        }
    }
}