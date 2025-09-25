package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(schema = "TMDV", name = "CHATBOT")
public class Chatbot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOG_ID")
    private Long id;

    @Column(name = "USUARIO_ID")
    private Long usuarioId;

    @Column(name = "MENSAJE_USUARIO", length = 4000)
    private String mensajeUsuario;

    @Column(name = "RESPUESTA_BOT", length = 4000)
    private String respuestaBot;

    @Column(name = "FECHA_HORA")
    private OffsetDateTime fechaHora;

    public Chatbot() {}

    public Chatbot(Long id, Long usuarioId, String mensajeUsuario,
                   String respuestaBot, OffsetDateTime fechaHora) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.mensajeUsuario = mensajeUsuario;
        this.respuestaBot = respuestaBot;
        this.fechaHora = fechaHora;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getMensajeUsuario() { return mensajeUsuario; }
    public void setMensajeUsuario(String mensajeUsuario) { this.mensajeUsuario = mensajeUsuario; }
    public String getRespuestaBot() { return respuestaBot; }
    public void setRespuestaBot(String respuestaBot) { this.respuestaBot = respuestaBot; }
    public OffsetDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(OffsetDateTime fechaHora) { this.fechaHora = fechaHora; }
}
