package com.duoc.finanzas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ENTIDAD TRANSACCIÓN
 * Tabla: transacciones
 * Tipos: INGRESO, GASTO
 */
@Entity
@Table(name = "transacciones")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Tipo es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoTransaccion tipo;

    @NotNull(message = "Monto es obligatorio")
    @DecimalMin(value = "0.01", message = "Monto debe ser mayor a 0")
    @Column(precision = 10, scale = 2)
    private BigDecimal monto;

    @NotBlank(message = "Categoría es obligatoria")
    private String categoria;

    @NotBlank(message = "Descripción es obligatoria")
    private String descripcion;

    @Column(name = "fecha_transaccion")
    private LocalDateTime fechaTransaccion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Ubicación GPS (opcional)
    private Double latitud;
    private Double longitud;

    @Column(name = "ubicacion_texto")
    private String ubicacionTexto;

    // Foto del recibo (opcional)
    @Column(name = "ruta_foto")
    private String rutaFoto;

    // Relación con usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Enum para tipos de transacción
    public enum TipoTransaccion {
        INGRESO, GASTO
    }

    // Constructores
    public Transaccion() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaTransaccion = LocalDateTime.now();
    }

    public Transaccion(TipoTransaccion tipo, BigDecimal monto, String categoria,
                      String descripcion, Usuario usuario) {
        this();
        this.tipo = tipo;
        this.monto = monto;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.usuario = usuario;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoTransaccion getTipo() { return tipo; }
    public void setTipo(TipoTransaccion tipo) { this.tipo = tipo; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaTransaccion() { return fechaTransaccion; }
    public void setFechaTransaccion(LocalDateTime fechaTransaccion) { this.fechaTransaccion = fechaTransaccion; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getUbicacionTexto() { return ubicacionTexto; }
    public void setUbicacionTexto(String ubicacionTexto) { this.ubicacionTexto = ubicacionTexto; }

    public String getRutaFoto() { return rutaFoto; }
    public void setRutaFoto(String rutaFoto) { this.rutaFoto = rutaFoto; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    @Override
    public String toString() {
        return "Transaccion{id=" + id + ", tipo=" + tipo + ", monto=" + monto +
               ", categoria='" + categoria + "', descripcion='" + descripcion + "'}";
    }
}