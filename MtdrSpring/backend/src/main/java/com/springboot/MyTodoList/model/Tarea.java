package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TAREA", schema = "TMDV")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Oracle: GENERATED ALWAYS AS IDENTITY
    @Column(name = "TAREA_ID")
    private Long id;

    // ====== Campos principales ======
    @Column(name = "TITULO", length = 150)
    private String titulo;

    @Column(name = "DESCRIPCION", length = 2000)
    private String descripcion; // la usaremos para get/setDescription()

    @Column(name = "ESTADO", length = 50)
    private String estado;

    @Column(name = "PRIORIDAD") // NVARCHAR2(200) en DB -> String aquí
    private String prioridad;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    // Campo DESCRIPTION (más corto). Lo mantenemos separado para no confundir con DESCRIPCION
    @Column(name = "DESCRIPTION", length = 1000)
    private String descriptionCorto;

    // Estimaciones / horas reales
    @Column(name = "ESTIMATED_HOURS")
    private Double estimatedHours;

    @Column(name = "REAL_HOURS")
    private Double realHours;

    // Asignación / sprint / nombre asignado
    @Column(name = "ASSIGNEE_ID", length = 64)
    private String assigneeId;

    @Column(name = "ASSIGNEE_NAME", length = 150)
    private String assigneeName;

    @Column(name = "SPRINT_ID", length = 64)
    private String sprintId;

    // Fechas varias
    @Column(name = "FECHA_ASIGNACION")
    private LocalDate fechaAsignacion;

    @Column(name = "ULTIMO_ACCESO")
    private LocalDateTime ultimoAcceso;

    @Column(name = "COMPLETED_AT")
    private LocalDateTime completedAt;

    @Column(name = "FECHA_LIMITE")
    private LocalDate fechaLimite;

    // Columna “misteriosa” NEW_COLUMN_17 (TIMESTAMP) — la mapeo por si la usas
    @Column(name = "NEW_COLUMN_17")
    private LocalDateTime newColumn17;

    // Scoping por usuario
    @Column(name = "USER_EMAIL", length = 320)
    private String userEmail;

    // ====== Getters / Setters completos ======
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getDescriptionCorto() { return descriptionCorto; }
    public void setDescriptionCorto(String descriptionCorto) { this.descriptionCorto = descriptionCorto; }

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

    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDate fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDate getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }

    public LocalDateTime getNewColumn17() { return newColumn17; }
    public void setNewColumn17(LocalDateTime newColumn17) { this.newColumn17 = newColumn17; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    // ====== ALIAS (compatibilidad con Service/Bot) ======
    // title <-> titulo
    public String getTitle() { return getTitulo(); }
    public void setTitle(String title) { setTitulo(title); }

    // description <-> DESCRIPCION (larga)
    public String getDescription() { return getDescripcion(); }
    public void setDescription(String description) { setDescripcion(description); }

    // status <-> estado
    public String getStatus() { return getEstado(); }
    public void setStatus(String status) { setEstado(status); }

    // priority <-> prioridad
    public String getPriority() { return getPrioridad(); }
    public void setPriority(String priority) { setPrioridad(priority); }
}
