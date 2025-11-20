package com.springboot.MyTodoList.service;

import java.util.List;

import com.springboot.MyTodoList.dto.CompleteTaskDto;
import com.springboot.MyTodoList.dto.TaskCreateDto;
import com.springboot.MyTodoList.dto.UpdateTaskDto;
import com.springboot.MyTodoList.model.Tarea;

public interface TareaService {
    Tarea createFromDto(TaskCreateDto dto);

    // ✅ NUEVO: overload para registrar al creador (userEmail) al momento de crear
    Tarea createFromDto(TaskCreateDto dto, String ownerEmail);

    List<Tarea> listByAssignee(String assigneeId);
    Tarea completeTask(Long id, CompleteTaskDto dto);
    void delete(Long id);

    List<Tarea> listCompletedBySprint(String sprintId);

    List<Tarea> listCompletedByUserAndSprint(String assigneeId, String sprintId);

    // NUEVO
    Tarea updateTask(Long id, UpdateTaskDto dto);
    Tarea updateStatus(Long id, String status);
}
