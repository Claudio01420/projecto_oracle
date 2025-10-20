package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "NOTIFICACIONES", schema = "TMDV")
public class Notificacion {

    @Id
    @Column(name = "NOTIF_ID")          // <--- PK REAL
    // Si tu columna NOTIF_ID es IDENTITY en Oracle, deja esta línea; 
    // si NO lo es, comenta la siguiente anotación.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USUARIO_ID")
    private Long usuarioId;

    @Column(name = "TITULO")
    private String titulo;

    @Column(name = "MENSAJE")
    private String mensaje;

    @Column(name = "TIPO")
    private String tipo;

    @Column(name = "PRIORIDAD")
    private String prioridad;

    @Column(name = "FECHA_ENVIO")
    private OffsetDateTime fechaEnvio;

    @Column(name = "FECHA_LEIDO")
    private OffsetDateTime fechaLeido;

    // En tu tabla existe LEIDA ('S'/'N')
    @Column(name = "LEIDA")
    private String leida;

    @Column(name = "EQUIPO_ID")
    private Long equipoId;

    @Column(name = "CREADA_POR")
    private Long creadaPor;

    // ===== Getters/Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public OffsetDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(OffsetDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public OffsetDateTime getFechaLeido() { return fechaLeido; }
    public void setFechaLeido(OffsetDateTime fechaLeido) { this.fechaLeido = fechaLeido; }

    public String getLeida() { return leida; }
    public void setLeida(String leida) { this.leida = leida; }

    public Long getEquipoId() { return equipoId; }
    public void setEquipoId(Long equipoId) { this.equipoId = equipoId; }

    public Long getCreadaPor() { return creadaPor; }
    public void setCreadaPor(Long creadaPor) { this.creadaPor = creadaPor; }
}
