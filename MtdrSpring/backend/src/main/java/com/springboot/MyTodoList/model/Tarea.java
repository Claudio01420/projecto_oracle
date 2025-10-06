package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TAREA")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TAREA_ID")
    private Long id;

    @Column(name = "TITULO")
    private String title;

    // usamos la columna DESCRIPCION (en mayúsculas) de tu tabla
    @Column(name = "DESCRIPCION", length = 2000)
    private String description;

    // valores recomendados: todo | doing | done
    @Column(name = "ESTADO")
    private String status;

    @Column(name = "PRIORIDAD")
    private String priority;

    @Column(name = "ESTIMATED_HOURS")
    private Double estimatedHours;

    @Column(name = "REAL_HOURS")
    private Double realHours;

    @Column(name = "ASSIGNEE_ID")
    private String assigneeId;

    @Column(name = "ASSIGNEE_NAME")
    private String assigneeName;

    @Column(name = "SPRINT_ID")
    private String sprintId;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "COMPLETED_AT")
    private LocalDateTime completedAt;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Double getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Double estimatedHours) { this.estimatedHours = estimatedHours; }

    public Double getRealHours() { return realHours; }
    public void setRealHours(Double realHours) { this.realHours = realHours; }

    public String getAssigneeId() { return assigneeId; }
    public void setAssigneeId(String assigneeId) { this.assigneeId = assigneeId; }

    public String getAssigneeName() { return assigneeName; }
    public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }

    public String getSprintId() { return sprintId; }
    public void setSprintId(String sprintId) { this.sprintId = sprintId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}

