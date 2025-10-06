package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.dto.CompleteTaskDto;
import com.springboot.MyTodoList.dto.TaskCreateDto;
import com.springboot.MyTodoList.model.Tarea;

import java.util.List;

public interface TareaService {
    Tarea createFromDto(TaskCreateDto dto);
    List<Tarea> listByAssignee(String assigneeId);
    Tarea completeTask(Long id, CompleteTaskDto dto);
    void delete(Long id);

}

