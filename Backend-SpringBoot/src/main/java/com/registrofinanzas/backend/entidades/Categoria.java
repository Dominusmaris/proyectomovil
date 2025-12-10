/**
 * ════════════════════════════════════════════════════════════════════════════
 * 📂 CARPETA: entidades/
 * 📄 ARCHIVO: Categoria.java
 * ════════════════════════════════════════════════════════════════════════════
 *
 * 🎯 ¿QUÉ ES ESTO?
 * Entidad que representa categorías de gastos e ingresos en la base de datos.
 *
 * 📊 CAMPOS PRINCIPALES:
 * - id: Identificador único
 * - nombre: Nombre de la categoría (ej: "Comida", "Salario")
 * - tipo: "GASTO" o "INGRESO"
 * - descripcion: Descripción opcional
 * - fechaCreacion: Cuándo se creó
 * - estado: 'A'=Activa, 'I'=Inactiva
 * - usuario: Dueño de la categoría
 *
 * 🗄️ TABLA EN BD: categorias
 *
 * 🔗 SE USA EN:
 * - CategoriaRepository.java
 * - CategoriaController.java
 *
 * ════════════════════════════════════════════════════════════════════════════
 */
package com.registrofinanzas.backend.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.List;
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Tipo Integer requerido

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre; // Tipo String requerido

    @NotBlank(message = "El tipo es obligatorio")
    @Column(nullable = false, length = 20)
    private String tipo; // "GASTO" o "INGRESO" - Tipo String requerido

    @Column(length = 200)
    private String descripcion; // Tipo String opcional

    @Column(name = "fecha_creacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion; // Tipo Date requerido

    @Column(length = 1)
    private String estado; // Tipo Char requerido - 'A'=Activa, 'I'=Inactiva

    // Relación muchos a uno con Usuario (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relación uno a muchos con Transacciones
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaccion> transacciones;

    // Constructores
    public Categoria() {
        this.fechaCreacion = new Date();
        this.estado = "A"; // Activa por defecto
    }

    public Categoria(String nombre, String tipo, Usuario usuario) {
        this();
        this.nombre = nombre;
        this.tipo = tipo;
        this.usuario = usuario;
    }

    public Categoria(String nombre, String tipo, String descripcion, Usuario usuario) {
        this(nombre, tipo, usuario);
        this.descripcion = descripcion;
    }

    // Getters y Setters
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(List<Transaccion> transacciones) {
        this.transacciones = transacciones;
    }
}