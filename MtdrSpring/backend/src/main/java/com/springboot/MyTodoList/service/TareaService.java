package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.dto.CompleteTaskDto;
import com.springboot.MyTodoList.dto.TaskCreateDto;
import com.springboot.MyTodoList.dto.UpdateTaskDto;
import com.springboot.MyTodoList.model.Tarea;

import java.util.List;

public interface TareaService {
    Tarea createFromDto(TaskCreateDto dto);
    List<Tarea> listByAssignee(String assigneeId);
    Tarea completeTask(Long id, CompleteTaskDto dto);
    void delete(Long id);

    // NUEVO
    Tarea updateTask(Long id, UpdateTaskDto dto);
    Tarea updateStatus(Long id, String status);
}

