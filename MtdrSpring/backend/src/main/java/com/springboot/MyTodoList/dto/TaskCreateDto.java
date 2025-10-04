package com.springboot.MyTodoList.dto;

import javax.validation.constraints.*;

public class TaskCreateDto {
  @NotBlank @Size(min = 3, max = 150)
  public String title;

  @Size(max = 2000)
  public String description;

  @NotNull @DecimalMin("0.5") @DecimalMax("4.0")
  public Double estimatedHours;

  @NotBlank
  public String assigneeId;

  @NotBlank
  public String assigneeName;

  @NotBlank
  public String status;     // "todo" | "in_progress" | "done"

  @NotBlank
  public String priority;   // "low" | "medium" | "high"

  public String sprintId;   // opcional
  public Long projectId;    // opcional
}
