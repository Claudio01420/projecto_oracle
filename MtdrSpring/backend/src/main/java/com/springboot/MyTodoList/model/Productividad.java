package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(schema = "TMDV", name = "PRODUCTIVIDAD")
public class Productividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROD_ID")
    private Long id;

    @Column(name = "USUARIO_ID")
    private Long usuarioId;

    @Column(name = "TIEMPO_PANTALLA")
    private Integer tiempoPantalla;

    @Column(name = "TAREAS_COMPLETADAS")
    private Integer tareasCompletadas;

    @Column(name = "FECHA_REGISTRO")
    private LocalDate fechaRegistro;

    public Productividad() {}

    public Productividad(Long id, Long usuarioId, Integer tiempoPantalla,
                         Integer tareasCompletadas, LocalDate fechaRegistro) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tiempoPantalla = tiempoPantalla;
        this.tareasCompletadas = tareasCompletadas;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Integer getTiempoPantalla() { return tiempoPantalla; }
    public void setTiempoPantalla(Integer tiempoPantalla) { this.tiempoPantalla = tiempoPantalla; }
    public Integer getTareasCompletadas() { return tareasCompletadas; }
    public void setTareasCompletadas(Integer tareasCompletadas) { this.tareasCompletadas = tareasCompletadas; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
