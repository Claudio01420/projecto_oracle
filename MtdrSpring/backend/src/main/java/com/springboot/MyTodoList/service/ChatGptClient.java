package com.springboot.MyTodoList.service;

import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import com.springboot.MyTodoList.config.OpenAiProperties;

@Component
public class ChatGptClient {

    private static final Logger log = LoggerFactory.getLogger(ChatGptClient.class);

    private final RestTemplate restTemplate;
    private final OpenAiProperties props;

    public ChatGptClient(OpenAiProperties props, RestTemplateBuilder builder) {
        this.props = props;
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }

    public String createChatCompletion(List<Message> messages) {
        Assert.notNull(messages, "messages");
        if (!props.isConfigured()) {
            throw new IllegalStateException("OpenAI API key no configurada. Define openai.api.key.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(props.getApiKey());

        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(props.getModel());
        request.setMessages(messages);
        request.setTemperature(props.getTemperature());

        HttpEntity<ChatCompletionRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<ChatCompletionResponse> response = restTemplate
                .postForEntity(props.getBaseUrl() + "/chat/completions", entity, ChatCompletionResponse.class);

        ChatCompletionResponse body = response.getBody();
        if (body == null || body.getChoices() == null || body.getChoices().isEmpty()) {
            log.warn("OpenAI respondió sin choices");
            return "";
        }
        return body.getChoices().get(0).getMessage().getContent();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ChatCompletionRequest {
        private String model;
        private List<Message> messages;
        private Double temperature;

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public void setMessages(List<Message> messages) {
            this.messages = messages;
        }

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ChatCompletionResponse {
        private List<Choice> choices;

        public List<Choice> getChoices() {
            return choices;
        }

        public void setChoices(List<Choice> choices) {
            this.choices = choices;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Choice {
        private Message message;

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }

    @JsonPropertyOrder({"role", "content"})
    public static class Message {
        private String role;
        private String content;

        public Message() {}

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

