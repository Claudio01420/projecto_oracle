package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(schema = "TMDV", name = "SPRINT")
public class Sprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SPRINT_ID")
    private Long id;

    @Column(name = "PROYECTO_ID")
    private Long projectId;

    @Column(name = "TITULO_SPRINT", length = 150)
    private String tituloSprint;

    @Column(name = "DESCRIPCION_SPRINT", length = 500)
    private String descripcionSprint;

    /**
     * En la BD la columna se llama DURACION y es DATE.
     * La usamos como FECHA FIN del sprint.
     */
    @Column(name = "DURACION")
    private LocalDate duracion;   // fecha fin

    @Column(name = "NUMERO")
    private Integer numero;

    @Column(name = "FECHA_INICIO")
    private LocalDate fechaInicio;

    public Sprint() {}

    public Sprint(Long id,
                  Long projectId,
                  String tituloSprint,
                  String descripcionSprint,
                  LocalDate duracion,
                  Integer numero,
                  LocalDate fechaInicio) {
        this.id = id;
        this.projectId = projectId;
        this.tituloSprint = tituloSprint;
        this.descripcionSprint = descripcionSprint;
        this.duracion = duracion;
        this.numero = numero;
        this.fechaInicio = fechaInicio;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getTituloSprint() { return tituloSprint; }
    public void setTituloSprint(String tituloSprint) { this.tituloSprint = tituloSprint; }

    public String getDescripcionSprint() { return descripcionSprint; }
    public void setDescripcionSprint(String descripcionSprint) { this.descripcionSprint = descripcionSprint; }

    public LocalDate getDuracion() { return duracion; }
    public void setDuracion(LocalDate duracion) { this.duracion = duracion; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
}
