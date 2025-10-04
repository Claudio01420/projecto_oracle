package com.springboot.MyTodoList.service;

import java.util.List;
import com.springboot.MyTodoList.dto.TaskCreateDto;
import com.springboot.MyTodoList.model.Tarea;

public interface TareaService {
  Tarea createFromDto(TaskCreateDto dto);
  List<Tarea> findAll();
}
