package com.springboot.MyTodoList.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
public class BotProps {

    private String name;
    private String token;

    // Getters
    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    // Setters
    public void setToken(String token) {
        this.token = token;
    }

    public void setName(String name) {
        this.name = name;
    }
}