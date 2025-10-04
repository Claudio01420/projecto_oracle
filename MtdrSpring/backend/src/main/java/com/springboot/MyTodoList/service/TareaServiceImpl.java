package com.springboot.MyTodoList.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.MyTodoList.dto.TaskCreateDto;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.TareaRepository;

@Service
public class TareaServiceImpl implements TareaService {

  private final TareaRepository tareaRepository;

  public TareaServiceImpl(TareaRepository tareaRepository) {
    this.tareaRepository = tareaRepository;
  }

  @Override
  @Transactional
  public Tarea createFromDto(TaskCreateDto dto) {
    Tarea t = new Tarea();
    t.setTitulo(dto.title);
    t.setDescripcion(dto.description);
    t.setEstado(dto.status);
    t.setPrioridad(dto.priority);
    t.setProyectoId(dto.projectId);
    t.setEstimatedHours(dto.estimatedHours);
    t.setAssigneeId(dto.assigneeId);
    t.setAssigneeName(dto.assigneeName);
    t.setSprintId(dto.sprintId);
    t.setFechaAsignacion(LocalDate.now());
    t.setUltimoAcceso(LocalDateTime.now());
    return tareaRepository.save(t);
  }

  @Override
  public List<Tarea> findAll() {
    return tareaRepository.findAll();
  }
}
