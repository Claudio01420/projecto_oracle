package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(schema = "TMDV", name = "PROYECTOS")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROYECTO_ID")
    private Long id;

    @Column(name = "NOMBRE_PROYECTO", length = 250)
    private String nombreProyecto;

    @Column(name = "DESCRIPCION", length = 4000)
    private String descripcion;

    @Column(name = "ESTADO", length = 50)
    private String estado;

    @Column(name = "EQUIPO_ID")
    private Long equipoId;

    @Column(name = "FECHA_INICIO")
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDate fechaFin;

    @Column(name = "ULTIMO_ACCESO")
    private OffsetDateTime ultimoAcceso;

    // === NUEVO: id del usuario creador ===
    @Column(name = "CREADOR_ID")
    private Long creadorId;

    public Proyecto() {}

    public Proyecto(Long id, String nombreProyecto, String descripcion, String estado,
                    Long equipoId, LocalDate fechaInicio, LocalDate fechaFin,
                    OffsetDateTime ultimoAcceso, Long creadorId) {
        this.id = id;
        this.nombreProyecto = nombreProyecto;
        this.descripcion = descripcion;
        this.estado = estado;
        this.equipoId = equipoId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.ultimoAcceso = ultimoAcceso;
        this.creadorId = creadorId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreProyecto() { return nombreProyecto; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Long getEquipoId() { return equipoId; }
    public void setEquipoId(Long equipoId) { this.equipoId = equipoId; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public OffsetDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(OffsetDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
    public Long getCreadorId() { return creadorId; }
    public void setCreadorId(Long creadorId) { this.creadorId = creadorId; }
}
