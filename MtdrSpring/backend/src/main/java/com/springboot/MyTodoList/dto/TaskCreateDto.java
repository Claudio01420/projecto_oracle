package com.springboot.MyTodoList.dto;

public class TaskCreateDto {
    public String title;
    public String description;
    public Double estimatedHours;
    public String priority;
    public String status;
    public String sprintId;
    public Long projectId;   // <— obligatorio en create
    public String assigneeId;
    public String assigneeName;
}
