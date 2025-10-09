package com.springboot.MyTodoList.config;


import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.springboot.MyTodoList.controller.ToDoItemBotController;

@Configuration
public class TelegramBotConfig {

    private final ToDoItemBotController bot;

    public TelegramBotConfig(ToDoItemBotController bot) {
        this.bot = bot;
    }

    @PostConstruct
    public void init() throws Exception {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot);
    }
}