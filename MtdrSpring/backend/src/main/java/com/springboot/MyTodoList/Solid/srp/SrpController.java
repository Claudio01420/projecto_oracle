package com.springboot.MyTodoList.solid.srp;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/solid/srp")
public class SrpController {
    private final NotificationComposer composer;
    private final NotificationSender sender;

    public SrpController(NotificationComposer composer, NotificationSender sender) {
        this.composer = composer;
        this.sender = sender;
    }

    
    @GetMapping("/welcome")
    public String welcome(@RequestParam String nombre, @RequestParam String email) {
        String msg = composer.composeWelcome(nombre);
        return sender.send(email, msg);
    }
}
