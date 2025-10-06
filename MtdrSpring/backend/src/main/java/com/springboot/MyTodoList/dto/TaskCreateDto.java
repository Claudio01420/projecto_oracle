package com.springboot.MyTodoList.dto;

import javax.validation.constraints.*;
public class TaskCreateDto {
    @NotBlank @Size(max = 150)
    public String title;

    @Size(max = 2000)
    public String description;

    @NotNull @DecimalMin("0.0") @DecimalMax("4.0")
    public Double estimatedHours;

    @NotBlank
    public String priority;      // alta | media | baja (o lo que uses)

    public String status = "todo"; // default

    public String assigneeId;    // ej. dev-123
    public String assigneeName;  // ej. Juan Pérez
    public String sprintId;      // ej. SPR-1
}

