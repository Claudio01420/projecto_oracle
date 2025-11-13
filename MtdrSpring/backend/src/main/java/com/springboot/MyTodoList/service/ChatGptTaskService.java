package com.springboot.MyTodoList.service;

import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.springboot.MyTodoList.util.BotIntent;

@Service
public class ChatGptTaskService {

    private static final Logger log = LoggerFactory.getLogger(ChatGptTaskService.class);
    private static final String SYSTEM_PROMPT = String.join("\n",
            "Eres un asistente experto en productividad. Respondes exclusivamente en JSON compacto.",
            "Traduce cada mensaje del usuario a una instrucción estructurada para un servicio CRUD de tareas.",
            "Formato obligatorio:",
            "{",
            "  \"action\": \"GREETING|HELP|LIST_TASKS|CREATE_TASK|MARK_DONE|MARK_PENDING|DELETE_TASK|UPDATE_TITLE|ASK_CLARIFICATION|UNKNOWN\",",
            "  \"taskId\": number|null,",
            "  \"taskTitle\": string|null,",
            "  \"description\": string|null,",
            "  \"newTitle\": string|null,",
            "  \"status\": \"todo|doing|done|null\",",
            "  \"projectId\": number|null,",
            "  \"dueDate\": \"yyyy-MM-dd\"|null,",
            "  \"priority\": \"alta|media|baja|null\",",
            "  \"botReply\": string",
            "}",
            "Reglas:",
            "- Usa action CREATE_TASK cuando el usuario pida nuevas tareas. Debe incluir taskTitle, description, projectId y dueDate.",
            "- Si faltan datos (como projectId o dueDate), responde con action ASK_CLARIFICATION y en botReply explica exactamente qué necesitas.",
            "- Usa LIST_TASKS para preguntas sobre sus pendientes.",
            "- Usa MARK_DONE/MARK_PENDING/DELETE_TASK/UPDATE_TITLE cuando el usuario hable de terminar, reabrir, borrar o renombrar tareas. Incluye taskId.",
            "- Usa HELP para solicitudes de ayuda y GREETING para saludos.",
            "- botReply debe ser un mensaje natural (ES) para el usuario.",
            "- Nunca incluyas texto fuera del JSON. Nunca uses Markdown."
    );

    private final ChatGptClient chatGptClient;
    private final ObjectMapper mapper;

    public ChatGptTaskService(ChatGptClient chatGptClient) {
        this.chatGptClient = chatGptClient;
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public BotIntent interpret(long chatId, String userMessage) {
        try {
            var messages = List.of(
                    new ChatGptClient.Message("system", SYSTEM_PROMPT),
                    new ChatGptClient.Message("user",
                            "chatId:" + chatId + " | mensaje:" + userMessage));

            String content = chatGptClient.createChatCompletion(messages);
            if (!StringUtils.hasText(content)) {
                return BotIntent.unknown("No pude contactar al motor de IA.");
            }
            return mapper.readValue(content.trim(), BotIntent.class);
        } catch (Exception ex) {
            log.error("No se pudo interpretar la intención del bot", ex);
            return BotIntent.unknown("Hubo un problema interpretando tu solicitud.");
        }
    }
}

