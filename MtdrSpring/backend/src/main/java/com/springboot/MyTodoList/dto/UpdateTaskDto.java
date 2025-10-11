package com.springboot.MyTodoList.dto;

public class UpdateTaskDto {
    public String title;
    public String description;
    public Double estimatedHours;
    public String priority;
    public String status;
    public String sprintId;
    public Long projectId;   // <— opcional en update
    public String assigneeId;
    public String assigneeName;
}
