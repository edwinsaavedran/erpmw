package com.miempresa.erpmw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "t_detalle_orden_compra")
public class DetalleOrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_orden_compra")
    private Integer idDetalleOrdenCompra;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_orden_compra", referencedColumnName = "id_orden_compra", nullable = false)
    private OrdenCompra ordenCompra;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_producto_proveedor", referencedColumnName = "id_producto_proveedor", nullable = false)
    private ProductoProveedor productoProveedor;

    @NotNull
    @Min(1)
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotNull
    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "estado", length = 1) // Podría ser nullable o tener un valor por defecto
    private Character estado;

    // Constructores, Getters y Setters
    public DetalleOrdenCompra() {}

    public Integer getIdDetalleOrdenCompra() { return idDetalleOrdenCompra; }
    public void setIdDetalleOrdenCompra(Integer idDetalleOrdenCompra) { this.idDetalleOrdenCompra = idDetalleOrdenCompra; }
    public OrdenCompra getOrdenCompra() { return ordenCompra; }
    public void setOrdenCompra(OrdenCompra ordenCompra) { this.ordenCompra = ordenCompra; }
    public ProductoProveedor getProductoProveedor() { return productoProveedor; }
    public void setProductoProveedor(ProductoProveedor productoProveedor) { this.productoProveedor = productoProveedor; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public Character getEstado() { return estado; }
    public void setEstado(Character estado) { this.estado = estado; }
}
