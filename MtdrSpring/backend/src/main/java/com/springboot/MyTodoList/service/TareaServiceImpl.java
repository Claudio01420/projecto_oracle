package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.dto.CompleteTaskDto;
import com.springboot.MyTodoList.dto.TaskCreateDto;
import com.springboot.MyTodoList.dto.UpdateTaskDto;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.TareaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        if (dto.estimatedHours != null && dto.estimatedHours > 4.0) {
            throw new IllegalArgumentException("Máximo 4 horas por tarea. Divide en subtareas.");
        }
        Tarea t = new Tarea();
        t.setTitle(dto.title);
        t.setDescription(dto.description);
        t.setEstimatedHours(dto.estimatedHours);
        t.setPriority(dto.priority);
        t.setStatus(dto.status == null ? "todo" : dto.status.toLowerCase());
        t.setAssigneeId(dto.assigneeId);
        t.setAssigneeName(dto.assigneeName);
        t.setSprintId(dto.sprintId);
        t.setCreatedAt(LocalDateTime.now());
        return repo.save(t);
    }

    @Override
    public List<Tarea> listByAssignee(String assigneeId) {
        // usa el método ordenado si lo tienes, si no, el simple:
        try {
            return repo.findByAssigneeIdOrderByCreatedAtDesc(assigneeId);
        } catch (Exception ignore) {
            return repo.findByAssigneeId(assigneeId);
        }
    }

    @Override
    public Tarea completeTask(Long id, CompleteTaskDto dto) {
        Tarea t = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no existe"));
        t.setRealHours(dto.realHours);
        t.setCompletedAt(LocalDateTime.now());
        t.setStatus(dto.status == null ? "done" : dto.status.toLowerCase());
        return repo.save(t);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada");
        }
        repo.deleteById(id);
    }

    @Override
    public Tarea updateTask(Long id, UpdateTaskDto dto) {
        Tarea t = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no existe"));

        if (dto.title != null) t.setTitle(dto.title);
        if (dto.description != null) t.setDescription(dto.description);
        if (dto.priority != null) t.setPriority(dto.priority);
        if (dto.status != null) t.setStatus(dto.status.toLowerCase());
        if (dto.estimatedHours != null) {
            if (dto.estimatedHours > 4.0) throw new IllegalArgumentException("Máximo 4 horas por tarea.");
            t.setEstimatedHours(dto.estimatedHours);
        }
        if (dto.assigneeId != null) t.setAssigneeId(dto.assigneeId);
        if (dto.assigneeName != null) t.setAssigneeName(dto.assigneeName);
        if (dto.sprintId != null) t.setSprintId(dto.sprintId);

        return repo.save(t);
    }

    @Override
    public Tarea updateStatus(Long id, String status) {
        Tarea t = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no existe"));
        String s = status == null ? "todo" : status.toLowerCase();
        if (!s.equals("todo") && !s.equals("doing") && !s.equals("done")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido");
        }
        t.setStatus(s);
        if ("done".equals(s) && t.getCompletedAt() == null) {
            t.setCompletedAt(LocalDateTime.now());
        }
        return repo.save(t);
    }
}

