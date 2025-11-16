package com.springboot.MyTodoList.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BotIntent {

    public enum Action {
        GREETING,
        HELP,
        LIST_TASKS,
        CREATE_TASK,
        MARK_DONE,
        MARK_PENDING,
        DELETE_TASK,
        UPDATE_TITLE,
        ASK_CLARIFICATION,
        UNKNOWN
    }

    private Action action = Action.UNKNOWN;

    private Long taskId;
    private String taskTitle;
    private String description;
    private String newTitle;
    private String status;
    private Long projectId;
    @JsonProperty("dueDate")
    private String dueDateIso;
    private String priority;
    private String botReply;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNewTitle() {
        return newTitle;
    }

    public void setNewTitle(String newTitle) {
        this.newTitle = newTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getDueDateIso() {
        return dueDateIso;
    }

    public void setDueDateIso(String dueDateIso) {
        this.dueDateIso = dueDateIso;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getBotReply() {
        return botReply;
    }

    public void setBotReply(String botReply) {
        this.botReply = botReply;
    }

    public static BotIntent unknown(String message) {
        BotIntent intent = new BotIntent();
        intent.setAction(Action.UNKNOWN);
        intent.setBotReply(message);
        return intent;
    }
}

