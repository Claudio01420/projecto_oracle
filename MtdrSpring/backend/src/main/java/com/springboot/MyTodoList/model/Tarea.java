package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(schema = "TMDV", name = "TAREA")
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
    private OffsetDateTime ultimoAcceso;

    @Column(name = "PRIORIDAD", length = 200)
    private String prioridad;

    // --- Constructores ---
    public Tarea() {}

    public Tarea(Long id, String titulo, String descripcion, String estado,
                 Long proyectoId, LocalDate fechaAsignacion,
                 OffsetDateTime ultimoAcceso, String prioridad) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.proyectoId = proyectoId;
        this.fechaAsignacion = fechaAsignacion;
        this.ultimoAcceso = ultimoAcceso;
        this.prioridad = prioridad;
    }

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Long getProyectoId() { return proyectoId; }
    public void setProyectoId(Long proyectoId) { this.proyectoId = proyectoId; }

    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDate fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public OffsetDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(OffsetDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
}

