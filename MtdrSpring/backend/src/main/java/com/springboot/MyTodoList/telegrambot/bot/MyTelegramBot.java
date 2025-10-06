package com.springboot.MyTodoList.telegrambot.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ConditionalOnProperty(name = "telegram.bot.token") // Solo si hay token, se crea el bean
@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    private final String token;
    private final String username;

    public MyTelegramBot(
            @Value("${telegram.bot.token:}") String token,
            @Value("${telegram.bot.username:${telegram.bot.name:}}") String username // acepta username o name
    ) {
        this.token = token;
        this.username = username;
    }

    @Override public String getBotUsername() { return username; }
    @Override public String getBotToken() { return token; }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String text   = update.getMessage().getText();

            String reply = "/start".equalsIgnoreCase(text.trim())
                    ? "¡Hola! Bot básico activo."
                    : "Echo: " + text;

            try {
                execute(new SendMessage(chatId, reply));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}