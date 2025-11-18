package com.springboot.MyTodoList.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SprintCreateDto {

    @NotNull(message = "El projectId es obligatorio")
    public Long projectId;

    @NotBlank(message = "El título del sprint es obligatorio")
    public String tituloSprint;

    public String descripcionSprint;

    /**
     * FECHA FIN del sprint (se guarda en la columna DURACION).
     * La calculamos en el front con base a fechaInicio + días de duración.
     */
    public LocalDate duracion;

    /**
     * Número del sprint:
     * - Si viene null, el backend lo calcula (siguiente correlativo).
     */
    public Integer numero;

    /**
     * Fecha de inicio del sprint.
     */
    public LocalDate fechaInicio;

    public SprintCreateDto() {}
}
