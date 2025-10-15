package com.springboot.MyTodoList.model;

import javax.persistence.*;

@Entity
@Table(schema = "TMDV", name = "USUARIOS")
public class Usuario {

    // Constantes de roles
    public static final String ROLE_SCRUM_MASTER = "Scrum Master";
    public static final String ROLE_DEVELOPER = "Desarrollador";
    public static final String ROLE_SUPER_ADMIN = "Super Admin";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USUARIO_ID")
    private Long id;

    @Column(name = "NOMBRE", length = 100)
    private String nombre;

    @Column(name = "EMAIL", length = 150)
    private String email;

    // OJO: la columna en la BD se llama CONTRASEÑA (con ñ). El mapeo funciona.
    @Column(name = "CONTRASEÑA", length = 255)
    private String contrasenia;

    @Column(name = "ROL", length = 100)
    private String rol;

    @Column(name = "TELEFONO")
    private Long telefono;

    public Usuario() {}

    public Usuario(Long id, String nombre, String email, String contrasenia,
                   String rol, Long telefono) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasenia = contrasenia;
        this.rol = rol;
        this.telefono = telefono;
    }

    // === Getters/Setters ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // Atajo para compatibilidad con controladores que usan getUsuarioId()
    public Long getUsuarioId() { return id; }
    public void setUsuarioId(Long usuarioId) { this.id = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasenia() { return contrasenia; }
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Long getTelefono() { return telefono; }
    public void setTelefono(Long telefono) { this.telefono = telefono; }
}

