package com.springboot.MyTodoList.model;

import javax.persistence.*;

@Entity
@Table(schema = "TMDV", name = "AJUSTES")
public class Ajustes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AJUSTES")
    private Long id;

    @Column(name = "EQUIPOS", length = 2000)
    private String equipos;

    @Column(name = "IDIOMA", length = 20)
    private String idioma;

    @Column(name = "NOMBRE", length = 20)
    private String nombre;

    @Column(name = "CORREO", length = 20)
    private String correo;

    public Ajustes() { }

    public Ajustes(Long id, String equipos, String idioma, String nombre, String correo) {
        this.id = id;
        this.equipos = equipos;
        this.idioma = idioma;
        this.nombre = nombre;
        this.correo = correo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEquipos() { return equipos; }
    public void setEquipos(String equipos) { this.equipos = equipos; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}
