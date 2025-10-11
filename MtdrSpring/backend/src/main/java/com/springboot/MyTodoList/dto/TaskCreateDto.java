package com.springboot.MyTodoList.dto;

public class TaskCreateDto {
    public String title;
    public String description;
    public Double estimatedHours;
    public String priority;      // "alta" | "media" | "baja"
    public String status;        // "todo" | "doing" | "done"
    public String sprintId;

    // Dueño / asignación (el backend forzará assigneeId = X-User-Email)
    public String assigneeId;
    public String assigneeName;

    // OBLIGATORIO: proyecto
    public Long projectId;
}
