package com.springboot.MyTodoList.solid.srp;

import org.springframework.stereotype.Component;

@Component
public class NotificationSender {
    public String send(String to, String message) {
        // Aquí iría email/SMS/etc. Para demo devolvemos un “OK”.
        return "Enviado a " + to + ": " + message;
    }
}
