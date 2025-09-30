package com.springboot.MyTodoList.solid.srp;

import org.springframework.stereotype.Component;

@Component
public class NotificationComposer {
    public String composeWelcome(String nombre) {
        return "¡Bienvenido, " + nombre + "! Gracias por registrarte.";
    }
}
