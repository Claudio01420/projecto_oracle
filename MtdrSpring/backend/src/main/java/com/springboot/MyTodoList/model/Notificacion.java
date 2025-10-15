package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(schema = "TMDV", name = "NOTIFICACIONES")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTIF_ID")
    private Long id;

    @Column(name = "USUARIO_ID")
    private Long usuarioId;

    @Column(name = "MENSAJE", length = 4000)
    private String mensaje;

    @Column(name = "TIPO", length = 50)
    private String tipo; // "Proyectos" | "Tareas" | "Sprints" | "Equipo"

    @Column(name = "FECHA_ENVIO")
    private OffsetDateTime fechaEnvio;

    @Column(name = "PRIORIDAD", length = 400)
    private String prioridad;

    // NUEVO
    @Column(name = "LEIDA", length = 1)
    private String leida; // 'N' (default) | 'Y'

    @Column(name = "FECHA_LEIDO")
    private OffsetDateTime fechaLeido;

    public Notificacion() {}

    public Notificacion(Long id, Long usuarioId, String mensaje, String tipo,
                        OffsetDateTime fechaEnvio, String prioridad) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.fechaEnvio = fechaEnvio;
        this.prioridad = prioridad;
        this.leida = "N";
    }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public OffsetDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(OffsetDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getLeida() { return leida; }
    public void setLeida(String leida) { this.leida = leida; }

    public OffsetDateTime getFechaLeido() { return fechaLeido; }
    public void setFechaLeido(OffsetDateTime fechaLeido) { this.fechaLeido = fechaLeido; }
}
