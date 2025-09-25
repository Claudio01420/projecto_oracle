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

    @Column(name = "TAREA_ID")
    private Long tareaId;

    @Column(name = "TITULO_SPRINT", length = 150)
    private String tituloSprint;

    @Column(name = "DESCRIPCION_SPRINT", length = 500)
    private String descripcionSprint;

    @Column(name = "DURACION")
    private LocalDate duracion;

    @Column(name = "NUMERO")
    private Integer numero;

    public Sprint() {}

    public Sprint(Long id, Long tareaId, String tituloSprint, String descripcionSprint,
                  LocalDate duracion, Integer numero) {
        this.id = id;
        this.tareaId = tareaId;
        this.tituloSprint = tituloSprint;
        this.descripcionSprint = descripcionSprint;
        this.duracion = duracion;
        this.numero = numero;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTareaId() { return tareaId; }
    public void setTareaId(Long tareaId) { this.tareaId = tareaId; }
    public String getTituloSprint() { return tituloSprint; }
    public void setTituloSprint(String tituloSprint) { this.tituloSprint = tituloSprint; }
    public String getDescripcionSprint() { return descripcionSprint; }
    public void setDescripcionSprint(String descripcionSprint) { this.descripcionSprint = descripcionSprint; }
    public LocalDate getDuracion() { return duracion; }
    public void setDuracion(LocalDate duracion) { this.duracion = duracion; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
}
