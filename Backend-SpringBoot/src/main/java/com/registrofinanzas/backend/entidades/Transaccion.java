/**
 * ════════════════════════════════════════════════════════════════════════════
 * 📂 CARPETA: entidades/
 * 📄 ARCHIVO: Transaccion.java
 * ════════════════════════════════════════════════════════════════════════════
 *
 * 🎯 ¿QUÉ ES ESTO?
 * Entidad que representa transacciones financieras (gastos e ingresos) en la base de datos.
 *
 * 📊 CAMPOS PRINCIPALES:
 * - id: Identificador único
 * - monto: Cantidad de dinero (BigDecimal para precisión)
 * - tipo: "GASTO" o "INGRESO"
 * - descripcion: Qué se compró/recibió
 * - fechaTransaccion: Cuándo ocurrió
 * - fechaCreacion: Cuándo se registró
 * - rutaFoto: Foto del recibo (opcional)
 * - estado: 'A'=Activa, 'I'=Inactiva
 * - usuario: Dueño de la transacción
 * - categoria: Categoría asignada
 *
 * 🗄️ TABLA EN BD: transacciones
 *
 * 🔗 SE USA EN:
 * - TransaccionRepository.java
 * - TransaccionController.java
 *
 * ════════════════════════════════════════════════════════════════════════════
 */
package com.registrofinanzas.backend.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "transacciones")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @NotBlank(message = "El tipo es obligatorio")
    @Column(nullable = false, length = 20)
    private String tipo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 3, max = 200, message = "La descripción debe tener entre 3 y 200 caracteres")
    @Column(nullable = false, length = 200)
    private String descripcion;
    @Column(name = "fecha_transaccion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaTransaccion;

    @Column(name = "fecha_creacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "ruta_foto", length = 500)
    private String rutaFoto;

    @Column(length = 1)
    private String estado;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    public Transaccion() {
        this.fechaCreacion = new Date();
        this.estado = "A"; // Activa por defecto
        if (this.fechaTransaccion == null) {
            this.fechaTransaccion = new Date();
        }
    }

    public Transaccion(BigDecimal monto, String tipo, String descripcion, Usuario usuario, Categoria categoria) {
        this();
        this.monto = monto;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.usuario = usuario;
        this.categoria = categoria;
    }

    public Transaccion(BigDecimal monto, String tipo, String descripcion, Date fechaTransaccion, Usuario usuario, Categoria categoria) {
        this(monto, tipo, descripcion, usuario, categoria);
        this.fechaTransaccion = fechaTransaccion;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
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

    public Date getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(Date fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}
