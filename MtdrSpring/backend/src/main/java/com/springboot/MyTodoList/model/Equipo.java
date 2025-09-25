package com.springboot.MyTodoList.model;

import javax.persistence.*;

@Entity
@Table(schema = "TMDV", name = "EQUIPOS")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EQUIPO_ID")
    private Long id;

    @Column(name = "NOMBRE_EQUIPO", length = 150)
    private String nombreEquipo;

    @Column(name = "DESCRIPCION", length = 2000)
    private String descripcion;

    public Equipo() {}

    public Equipo(Long id, String nombreEquipo, String descripcion) {
        this.id = id;
        this.nombreEquipo = nombreEquipo;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreEquipo() { return nombreEquipo; }
    public void setNombreEquipo(String nombreEquipo) { this.nombreEquipo = nombreEquipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
