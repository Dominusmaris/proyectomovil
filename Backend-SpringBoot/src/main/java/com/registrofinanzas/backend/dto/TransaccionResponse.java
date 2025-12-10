package com.registrofinanzas.backend.dto;

import java.math.BigDecimal;
import java.util.Date;

public class TransaccionResponse {
    private Integer id;
    private BigDecimal monto;
    private String descripcion;
    private String tipo;
    private Date fechaTransaccion;
    private String categoriaNombre;
    private String usuarioNombre;
    private String estado;

    public TransaccionResponse() {
    }

    public TransaccionResponse(Integer id, BigDecimal monto, String descripcion, String tipo,
                              Date fechaTransaccion, String categoriaNombre, String usuarioNombre, String estado) {
        this.id = id;
        this.monto = monto;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.fechaTransaccion = fechaTransaccion;
        this.categoriaNombre = categoriaNombre;
        this.usuarioNombre = usuarioNombre;
        this.estado = estado;
    }

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(Date fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}