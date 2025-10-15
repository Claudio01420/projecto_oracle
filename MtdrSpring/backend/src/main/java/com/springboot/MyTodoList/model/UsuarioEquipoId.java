package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UsuarioEquipoId implements Serializable {

    @Column(name = "USUARIO_ID")
    private Long usuarioId;

    @Column(name = "EQUIPO_ID")
    private Long equipoId;

    public UsuarioEquipoId() {}

    public UsuarioEquipoId(Long usuarioId, Long equipoId) {
        this.usuarioId = usuarioId;
        this.equipoId = equipoId;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getEquipoId() { return equipoId; }
    public void setEquipoId(Long equipoId) { this.equipoId = equipoId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioEquipoId)) return false;
        UsuarioEquipoId that = (UsuarioEquipoId) o;
        return Objects.equals(usuarioId, that.usuarioId) &&
               Objects.equals(equipoId, that.equipoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, equipoId);
    }
}
