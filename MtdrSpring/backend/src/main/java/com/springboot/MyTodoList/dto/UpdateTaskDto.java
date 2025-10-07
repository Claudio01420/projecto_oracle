package com.springboot.MyTodoList.dto;

public class UpdateTaskDto {
    public String title;
    public String description;
    public String priority;    // alta | media | baja
    public String status;      // todo | doing | done
    public Double estimatedHours;
    public String assigneeId;
    public String assigneeName;
    public String sprintId;
}
