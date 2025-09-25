package com.springboot.MyTodoList.model;

import javax.persistence.*;

@Entity
@Table(schema = "TMDV", name = "USUARIOS_EQUIPOS")
public class UsuarioEquipo {

    @EmbeddedId
    private UsuarioEquipoId id;

    public UsuarioEquipo() {}
    public UsuarioEquipo(UsuarioEquipoId id) { this.id = id; }

    public UsuarioEquipoId getId() { return id; }
    public void setId(UsuarioEquipoId id) { this.id = id; }
}
