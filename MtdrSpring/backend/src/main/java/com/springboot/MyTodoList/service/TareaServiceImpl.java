package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.dto.CompleteTaskDto;
import com.springboot.MyTodoList.dto.TaskCreateDto;
import com.springboot.MyTodoList.dto.UpdateTaskDto;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.TareaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TareaServiceImpl implements TareaService {

    private final TareaRepository repo;

    public TareaServiceImpl(TareaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Tarea createFromDto(TaskCreateDto dto) {
        Tarea t = new Tarea();
        t.setTitle(dto.title);
        t.setDescription(dto.description);
        t.setEstimatedHours(dto.estimatedHours);
        t.setPriority(dto.priority);
        t.setStatus(dto.status != null ? dto.status.toLowerCase() : "todo");
        t.setSprintId(dto.sprintId);
        t.setAssigneeId(dto.assigneeId);
        t.setAssigneeName(dto.assigneeName);
        t.setCreatedAt(LocalDateTime.now());

        // NUEVO: projectId
        t.setProjectId(dto.projectId);

        return repo.save(t);
    }

    @Override
    public List<Tarea> listByAssignee(String assigneeId) {
        return repo.findByAssigneeIdOrderByCreatedAtDesc(assigneeId);
    }

    @Override
    public Tarea completeTask(Long id, CompleteTaskDto dto) {
        Tarea t = repo.findById(id).orElseThrow();
        t.setRealHours(dto.realHours);
        t.setCompletedAt(LocalDateTime.now());
        if (dto.status != null) t.setStatus(dto.status.toLowerCase());
        return repo.save(t);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public Tarea updateTask(Long id, UpdateTaskDto dto) {
        Tarea t = repo.findById(id).orElseThrow();

        if (dto.title != null) t.setTitle(dto.title);
        if (dto.description != null) t.setDescription(dto.description);
        if (dto.estimatedHours != null) t.setEstimatedHours(dto.estimatedHours);
        if (dto.priority != null) t.setPriority(dto.priority);
        if (dto.status != null) t.setStatus(dto.status.toLowerCase());
        if (dto.sprintId != null) t.setSprintId(dto.sprintId);
        if (dto.assigneeId != null) t.setAssigneeId(dto.assigneeId);
        if (dto.assigneeName != null) t.setAssigneeName(dto.assigneeName);

        // NUEVO: projectId (si viene)
        if (dto.projectId != null) t.setProjectId(dto.projectId);

        return repo.save(t);
    }

    @Override
    public Tarea updateStatus(Long id, String status) {
        Tarea t = repo.findById(id).orElseThrow();
        if (status != null) {
            t.setStatus(status.toLowerCase());
            if ("done".equalsIgnoreCase(status) && t.getCompletedAt() == null) {
                t.setCompletedAt(LocalDateTime.now());
            }
        }
        return repo.save(t);
    }
}

