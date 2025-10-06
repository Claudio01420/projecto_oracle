package com.springboot.MyTodoList.dto;

import javax.validation.constraints.*;

public class CompleteTaskDto {
    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("24.0")
    public Double realHours;

    // opcional: permitir ajustar el estado explícitamente, por si usas otro flujo
    public String status = "done";
}
