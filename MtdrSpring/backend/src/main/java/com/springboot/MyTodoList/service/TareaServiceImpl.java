package com.springboot.MyTodoList.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.MyTodoList.dto.CompleteTaskDto;
import com.springboot.MyTodoList.dto.TaskCreateDto;
import com.springboot.MyTodoList.dto.UpdateTaskDto;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.TareaRepository;

@Service
@Transactional
public class TareaServiceImpl implements TareaService {

    private final TareaRepository tareaRepository;

    public TareaServiceImpl(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    /**
     * Normaliza cualquier variante a los 3 valores canónicos que usa el front.
     */
    private static String canonStatus(String s) {
        if (s == null) {
            return null;
        }
        String v = s.trim().toLowerCase();
        switch (v) {
            case "por hacer":
                return "todo";
            case "en progreso":
                return "doing";
            case "hecho":
                return "done";
            case "todo":
            case "doing":
            case "done":
                return v;
            default:
                return v; // deja pasar otros valores en minúscula
        }
    }

    @Override
    public Tarea createFromDto(TaskCreateDto dto) {
        // Mantener compatibilidad: si no recibimos ownerEmail explícito
        return createFromDto(dto, null);
    }

    // ✅ NUEVO: crear y setear userEmail (dueño/creador) sin perder tus campos extras
    @Override
    public Tarea createFromDto(TaskCreateDto dto, String ownerEmail) {
        Tarea t = new Tarea();
        t.setTitle(dto.title);
        t.setDescription(dto.description);
        t.setEstimatedHours(dto.estimatedHours);
        t.setPriority(dto.priority);
        t.setStatus(canonStatus(dto.status == null ? "todo" : dto.status));
        t.setSprintId(dto.sprintId);
        t.setProjectId(dto.projectId);
        t.setAssigneeId(dto.assigneeId);
        t.setAssigneeName(dto.assigneeName);

        // ⬅️ conservas tu fecha límite
        t.setFechaLimite(dto.fechaLimite);

        // timestamps tuyos
        t.setCreatedAt(LocalDateTime.now());
        t.setFechaAsignacion(LocalDate.now());

        // ⬅️ NUEVO: guardar quién creó la tarea
        if (ownerEmail != null && !ownerEmail.isBlank()) {
            t.setUserEmail(ownerEmail);
        }

        return tareaRepository.save(t);
    }

    @Override
    public List<Tarea> listByAssignee(String assigneeId) {
        return tareaRepository.findByAssigneeIdOrderByCreatedAtDesc(assigneeId);
    }

    @Override
    public Tarea completeTask(Long id, CompleteTaskDto dto) {
        Tarea t = tareaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada: " + id));
        t.setRealHours(dto.realHours);
        t.setCompletedAt(LocalDateTime.now()); // ⬅️ aquí ya guardas fecha/hora de completado
        t.setStatus("done");
        return tareaRepository.save(t);
    }

    @Override
    public void delete(Long id) {
        tareaRepository.deleteById(id);
    }

    @Override
    public Tarea updateTask(Long id, UpdateTaskDto dto) {
        Tarea t = tareaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada: " + id));

        if (dto.title != null) {
            t.setTitle(dto.title);
        }
        if (dto.description != null) {
            t.setDescription(dto.description);
        }
        if (dto.estimatedHours != null) {
            t.setEstimatedHours(dto.estimatedHours);
        }
        if (dto.priority != null) {
            t.setPriority(dto.priority);
        }
        if (dto.sprintId != null) {
            t.setSprintId(dto.sprintId);
        }
        if (dto.projectId != null) {
            t.setProjectId(dto.projectId);
        }
        if (dto.assigneeId != null) {
            t.setAssigneeId(dto.assigneeId);
        }
        if (dto.assigneeName != null) {
            t.setAssigneeName(dto.assigneeName);
        }

        // Si luego agregas fechaLimite al UpdateTaskDto, habilita:
        // if (dto.fechaLimite != null)     t.setFechaLimite(dto.fechaLimite);
        if (dto.status != null) {
            String s = canonStatus(dto.status);
            t.setStatus(s);
            if ("done".equals(s)) {
                if (t.getCompletedAt() == null) {
                    t.setCompletedAt(LocalDateTime.now());
                }
                // realHours se fija con /complete
            } else {
                // Al descompletar, limpia marcas y horas reales
                t.setCompletedAt(null);
                t.setRealHours(null);
            }
        }

        return tareaRepository.save(t);
    }

    @Override
    public Tarea updateStatus(Long id, String status) {
        Tarea t = tareaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada: " + id));

        String s = canonStatus(status);
        t.setStatus(s);
        if ("done".equals(s)) {
            if (t.getCompletedAt() == null) {
                t.setCompletedAt(LocalDateTime.now()); // ⬅️ también aquí
            }            // realHours se fija con /complete
        } else {
            // Si sale de 'done', resetear completado y horas reales
            t.setCompletedAt(null);
            t.setRealHours(null);
        }
        return tareaRepository.save(t);
    }

    @Override
    public List<Tarea> listCompletedBySprint(String sprintId) {
        // "done" es tu estado canónico
        return tareaRepository.findBySprintIdAndStatusIgnoreCaseOrderByCreatedAtDesc(sprintId, "done");
    }

    @Override
    public List<Tarea> listCompletedByUserAndSprint(String assigneeId, String sprintId) {
        return tareaRepository.findByAssigneeIdAndSprintIdAndStatusIgnoreCaseOrderByCreatedAtDesc(
                assigneeId,
                sprintId,
                "done"
        );
    }

}
