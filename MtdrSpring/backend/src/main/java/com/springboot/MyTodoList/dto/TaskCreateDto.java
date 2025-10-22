package com.springboot.MyTodoList.dto;

import java.time.LocalDate;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class TaskCreateDto {
    @NotBlank
    public String title;

    @NotBlank
    public String description;

    @DecimalMin("0.0")
    @DecimalMax("4.0")
    public Double estimatedHours;  // opcional, pero si viene, <= 4

    public String priority;        // opcional
    public String status;          // por defecto "todo" si no llega
    public String sprintId;        // opcional

    @NotNull
    public Long projectId;

    // El backend forzará assigneeId = owner (X-User-Email) igualmente
    public String assigneeId;      // opcional
    public String assigneeName;    // opcional

    // ⬅️ NUEVO: requerida por tu vista de proyectos
    @NotNull
    public LocalDate fechaLimite;
}

