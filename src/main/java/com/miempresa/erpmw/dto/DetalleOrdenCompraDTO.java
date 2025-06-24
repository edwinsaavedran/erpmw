package com.miempresa.erpmw.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class DetalleOrdenCompraDTO {

    private Integer idDetalleOrdenCompra; // Para futuras ediciones

    @NotNull(message = "Debe seleccionar un ítem de producto/proveedor.")
    private Integer idProductoProveedor;

    // Estos campos son opcionales en el DTO si se van a buscar/mostrar en la vista, pero no se envían para guardar
    private String nombreProductoCompleto; // Ej: "Producto (Marca, Proveedor)" para mostrar
    private BigDecimal precioUnitarioItem; // Para mostrar o recalcular en el front

    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad debe ser al menos 1.")
    private Integer cantidad;

    private BigDecimal subtotal; // Calculado, pero puede venir del front para verificación

    // Getters y Setters
    public Integer getIdDetalleOrdenCompra() {
        return idDetalleOrdenCompra;
    }

    public void setIdDetalleOrdenCompra(Integer idDetalleOrdenCompra) {
        this.idDetalleOrdenCompra = idDetalleOrdenCompra;
    }

    public Integer getIdProductoProveedor() {
        return idProductoProveedor;
    }

    public void setIdProductoProveedor(Integer idProductoProveedor) {
        this.idProductoProveedor = idProductoProveedor;
    }

    public String getNombreProductoCompleto() {
        return nombreProductoCompleto;
    }

    public void setNombreProductoCompleto(String nombreProductoCompleto) {
        this.nombreProductoCompleto = nombreProductoCompleto;
    }
    
    public BigDecimal getPrecioUnitarioItem() {
        return precioUnitarioItem;
    }

    public void setPrecioUnitarioItem(BigDecimal precioUnitarioItem) {
        this.precioUnitarioItem = precioUnitarioItem;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}