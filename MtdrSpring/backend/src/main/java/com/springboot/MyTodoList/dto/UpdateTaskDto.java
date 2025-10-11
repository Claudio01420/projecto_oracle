package com.springboot.MyTodoList.dto;

public class UpdateTaskDto {
    public String title;
    public String description;
    public Double estimatedHours;
    public String priority;
    public String status;
    public String sprintId;

    public String assigneeId;    // se forzará al dueño real
    public String assigneeName;

    // puede venir nulo, pero si viene debe ser > 0
    public Long projectId;
}
