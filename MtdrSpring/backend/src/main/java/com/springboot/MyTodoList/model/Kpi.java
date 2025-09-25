package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(schema = "TMDV", name = "KPIS")
public class Kpi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KPI_ID")
    private Long id;

    @Column(name = "PROYECTO_ID")
    private Long proyectoId;

    @Column(name = "VELOCIDAD_EQUIPO")
    private Integer velocidadEquipo;

    @Column(name = "CUMPLIMIENTO_SPRINTS")
    private Integer cumplimientoSprints;

    @Column(name = "EFICIENCIA")
    private Integer eficiencia;

    @Column(name = "FECHA")
    private LocalDate fecha;

    @Column(name = "TIPO", length = 400)
    private String tipo;

    @Column(name = "VALOR", length = 400)
    private String valor;

    @Column(name = "UNIDAD")
    private Integer unidad;

    @Column(name = "COMENTARIO", length = 4000)
    private String comentario;

    public Kpi() {}

    public Kpi(Long id, Long proyectoId, Integer velocidadEquipo, Integer cumplimientoSprints,
               Integer eficiencia, LocalDate fecha, String tipo, String valor,
               Integer unidad, String comentario) {
        this.id = id;
        this.proyectoId = proyectoId;
        this.velocidadEquipo = velocidadEquipo;
        this.cumplimientoSprints = cumplimientoSprints;
        this.eficiencia = eficiencia;
        this.fecha = fecha;
        this.tipo = tipo;
        this.valor = valor;
        this.unidad = unidad;
        this.comentario = comentario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProyectoId() { return proyectoId; }
    public void setProyectoId(Long proyectoId) { this.proyectoId = proyectoId; }
    public Integer getVelocidadEquipo() { return velocidadEquipo; }
    public void setVelocidadEquipo(Integer velocidadEquipo) { this.velocidadEquipo = velocidadEquipo; }
    public Integer getCumplimientoSprints() { return cumplimientoSprints; }
    public void setCumplimientoSprints(Integer cumplimientoSprints) { this.cumplimientoSprints = cumplimientoSprints; }
    public Integer getEficiencia() { return eficiencia; }
    public void setEficiencia(Integer eficiencia) { this.eficiencia = eficiencia; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
    public Integer getUnidad() { return unidad; }
    public void setUnidad(Integer unidad) { this.unidad = unidad; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
