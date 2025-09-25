package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Chatbot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatbotRepository extends JpaRepository<Chatbot, Long> {}
