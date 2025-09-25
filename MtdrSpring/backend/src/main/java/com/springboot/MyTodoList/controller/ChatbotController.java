package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Chatbot;
import com.springboot.MyTodoList.repository.ChatbotRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    private final ChatbotRepository repo;
    public ChatbotController(ChatbotRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Chatbot> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Chatbot one(@PathVariable Long id) { return repo.findById(id).orElse(null); }

    @PostMapping
    public Chatbot create(@RequestBody Chatbot c) { return repo.save(c); }
}
