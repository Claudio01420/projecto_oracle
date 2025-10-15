package com.springboot.MyTodoList.model;

import javax.persistence.*;

@Entity
@Table(
    schema = "TMDV",
    name = "USUARIOS_EQUIPOS",
    uniqueConstraints = @UniqueConstraint(columnNames = {"USUARIO_ID", "EQUIPO_ID"})
)
public class UsuarioEquipo {

    @EmbeddedId
    private UsuarioEquipoId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROL", length = 30, nullable = false)
    private RolEquipo rol = RolEquipo.USUARIO;

    public UsuarioEquipo() {}

    public UsuarioEquipo(UsuarioEquipoId id) {
        this.id = id;
    }

    public UsuarioEquipo(UsuarioEquipoId id, RolEquipo rol) {
        this.id = id;
        this.rol = rol;
    }

    public UsuarioEquipoId getId() { return id; }
    public void setId(UsuarioEquipoId id) { this.id = id; }

    public RolEquipo getRol() { return rol; }
    public void setRol(RolEquipo rol) { this.rol = rol; }
}
