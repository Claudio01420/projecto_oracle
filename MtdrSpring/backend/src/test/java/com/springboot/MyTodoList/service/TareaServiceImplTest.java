package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.dto.TaskCreateDto;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.TareaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de TareaServiceImpl usando Mockito.
 */
@ExtendWith(MockitoExtension.class)
class TareaServiceImplTest {

    @Mock
    private TareaRepository tareaRepository;

    @InjectMocks
    private TareaServiceImpl tareaService;

    // 1) CREAR TAREA
    @Test
    void createFromDto_debe_mapear_campos_y_guardar_con_ownerEmail() {
        // Arrange
        TaskCreateDto dto = new TaskCreateDto();
        dto.title = "Nueva tarea";
        dto.description = "Desc prueba";
        dto.estimatedHours = 2.5;
        dto.priority = "HIGH";
        dto.status = null; // debe caer en "todo"
        dto.sprintId = "SPR-1";
        dto.projectId = 1L;
        dto.fechaLimite = LocalDate.now().plusDays(3);

        // mock de save: regresa la misma tarea con id seteado
        when(tareaRepository.save(any(Tarea.class))).thenAnswer(invocation -> {
            Tarea t = invocation.getArgument(0);
            t.setId(100L);
            return t;
        });

        String ownerEmail = "owner@example.com";

        // Act
        Tarea result = tareaService.createFromDto(dto, ownerEmail);

        // Assert
        ArgumentCaptor<Tarea> captor = ArgumentCaptor.forClass(Tarea.class);
        verify(tareaRepository).save(captor.capture());
        Tarea saved = captor.getValue();

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(saved.getTitle()).isEqualTo("Nueva tarea");
        assertThat(saved.getDescription()).isEqualTo("Desc prueba");
        assertThat(saved.getEstimatedHours()).isEqualTo(2.5);
        // status por defecto "todo" (canonStatus)
        assertThat(saved.getStatus()).isEqualTo("todo");
        // sprint y proyecto
        assertThat(saved.getSprintId()).isEqualTo("SPR-1");
        assertThat(saved.getProjectId()).isEqualTo(1L);
        // fecha límite
        assertThat(saved.getFechaLimite()).isEqualTo(dto.fechaLimite);
        // dueño/creador
        assertThat(saved.getUserEmail()).isEqualTo(ownerEmail);
        // createdAt y fechaAsignacion NO nulos
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getFechaAsignacion()).isNotNull();
    }

    // 2) VER TAREAS COMPLETADAS DE UN SPRINT
    @Test
    void listCompletedBySprint_debe_llamar_repo_con_estado_done() {
        // Arrange
        String sprintId = "SPR-1";

        Tarea t1 = new Tarea();
        t1.setId(1L);
        t1.setStatus("done");

        Tarea t2 = new Tarea();
        t2.setId(2L);
        t2.setStatus("done");

        when(tareaRepository
                .findBySprintIdAndStatusIgnoreCaseOrderByCreatedAtDesc(sprintId, "done"))
                .thenReturn(List.of(t1, t2));

        // Act
        List<Tarea> result = tareaService.listCompletedBySprint(sprintId);

        // Assert
        verify(tareaRepository)
                .findBySprintIdAndStatusIgnoreCaseOrderByCreatedAtDesc(sprintId, "done");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Tarea::getId)
                .containsExactly(1L, 2L);
    }

    // 3) VER TAREAS COMPLETADAS DE UN USUARIO EN UN SPRINT
    @Test
    void listCompletedByUserAndSprint_debe_llamar_repo_con_estado_done() {
        // Arrange
        String sprintId = "SPR-1";
        String assigneeId = "user-123";

        Tarea t1 = new Tarea();
        t1.setId(10L);
        t1.setStatus("done");
        t1.setAssigneeId(assigneeId);

        when(tareaRepository
                .findByAssigneeIdAndSprintIdAndStatusIgnoreCaseOrderByCreatedAtDesc(
                        assigneeId, sprintId, "done"))
                .thenReturn(List.of(t1));

        // Act
        List<Tarea> result = tareaService.listCompletedByUserAndSprint(assigneeId, sprintId);

        // Assert
        verify(tareaRepository)
                .findByAssigneeIdAndSprintIdAndStatusIgnoreCaseOrderByCreatedAtDesc(
                        assigneeId, sprintId, "done");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(10L);
        assertThat(result.get(0).getAssigneeId()).isEqualTo(assigneeId);
    }
}
