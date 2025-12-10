/**
 * ════════════════════════════════════════════════════════════════════════════
 * 📂 CARPETA: entidades/
 * 📄 ARCHIVO: Usuario.java
 * ════════════════════════════════════════════════════════════════════════════
 *
 * 🎯 ¿QUÉ ES ESTO?
 * Entidad que representa un usuario en la base de datos.
 *
 * 📊 CAMPOS PRINCIPALES:
 * - id: Identificador único
 * - nombre: Nombre completo del usuario
 * - email: Email único para login
 * - password: Contraseña encriptada
 * - fechaRegistro: Cuándo se registró
 * - rol: ADMIN o USUARIO
 * - activo: Si la cuenta está activa
 *
 * 🗄️ TABLA EN BD: usuarios
 *
 * 🔗 SE USA EN:
 * - UsuarioRepository.java
 * - UsuarioController.java
 * - AuthController.java
 *
 * ════════════════════════════════════════════════════════════════════════════
 */
package com.registrofinanzas.backend.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.List;


@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email debe tener formato válido")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;

    @Column(name = "fecha_registro", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @Column(nullable = false, length = 20)
    private String rol = "USUARIO"; // "ADMIN" o "USUARIO"

    @Column(name = "activo")
    private boolean activo = true;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Categoria> categorias;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaccion> transacciones;

    public Usuario() {
        this.fechaRegistro = new Date();
        this.rol = "USUARIO";
        this.activo = true;
    }

    public Usuario(String nombre, String email, String password) {
        this();
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(List<Transaccion> transacciones) {
        this.transacciones = transacciones;
    }
}