package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TAREA", schema = "TMDV") // <-- usa tu esquema real
public class Tarea {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "TAREA_ID")
  private Long id;

  @Column(name = "TITULO", length = 150)
  private String titulo;

  @Column(name = "DESCRIPCION", length = 2000)
  private String descripcion;

  @Column(name = "ESTADO", length = 50)
  private String estado;

  @Column(name = "PROYECTO_ID")
  private Long proyectoId;

  @Column(name = "FECHA_ASIGNACION")
  private LocalDate fechaAsignacion;

  @Column(name = "ULTIMO_ACCESO")
  private LocalDateTime ultimoAcceso;

  @Column(name = "PRIORIDAD")
  private String prioridad;

  @Column(name = "ESTIMATED_HOURS")
  private Double estimatedHours;

  @Column(name = "ASSIGNEE_ID", length = 64)
  private String assigneeId;

  @Column(name = "ASSIGNEE_NAME", length = 150)
  private String assigneeName;

  @Column(name = "SPRINT_ID", length = 64)
  private String sprintId;

  public Tarea() {}

  // getters/setters
  public Long getId() { return id; } public void setId(Long id) { this.id = id; }
  public String getTitulo() { return titulo; } public void setTitulo(String titulo) { this.titulo = titulo; }
  public String getDescripcion() { return descripcion; } public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
  public String getEstado() { return estado; } public void setEstado(String estado) { this.estado = estado; }
  public Long getProyectoId() { return proyectoId; } public void setProyectoId(Long proyectoId) { this.proyectoId = proyectoId; }
  public LocalDate getFechaAsignacion() { return fechaAsignacion; } public void setFechaAsignacion(LocalDate fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }
  public LocalDateTime getUltimoAcceso() { return ultimoAcceso; } public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
  public String getPrioridad() { return prioridad; } public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
  public Double getEstimatedHours() { return estimatedHours; } public void setEstimatedHours(Double estimatedHours) { this.estimatedHours = estimatedHours; }
  public String getAssigneeId() { return assigneeId; } public void setAssigneeId(String assigneeId) { this.assigneeId = assigneeId; }
  public String getAssigneeName() { return assigneeName; } public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }
  public String getSprintId() { return sprintId; } public void setSprintId(String sprintId) { this.sprintId = sprintId; }
}
