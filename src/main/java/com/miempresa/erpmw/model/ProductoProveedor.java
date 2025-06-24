package com.miempresa.erpmw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "t_producto_proveedor",
       uniqueConstraints = @UniqueConstraint(columnNames = {"fk_producto", "fk_proveedor"}, name = "uk_prod_prov_producto_proveedor"))
public class ProductoProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto_proveedor")
    private Integer idProductoProveedor;

    @NotNull(message = "Debe seleccionar un producto.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_producto", referencedColumnName = "id_producto", nullable = false)
    private Producto producto;

    @NotNull(message = "Debe seleccionar un proveedor.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_proveedor", referencedColumnName = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @NotNull(message = "El stock no puede ser nulo.")
    @PositiveOrZero(message = "El stock no puede ser negativo.")
    @Column(name = "stock")
    private Integer stock;

    @NotNull(message = "El precio unitario no puede ser nulo.")
    @PositiveOrZero(message = "El precio unitario no puede ser negativo.")
    @Column(name = "precio_unitario", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "estado", length = 1, nullable = false)
    private Character estado; // 'A': Activo, 'I': Inactivo

    @OneToMany(mappedBy = "productoProveedor", fetch = FetchType.LAZY)
    private Set<DetalleOrdenCompra> detalleOrdenCompras;

    // Constructores, Getters y Setters
    public ProductoProveedor() {}

    public Integer getIdProductoProveedor() { return idProductoProveedor; }
    public void setIdProductoProveedor(Integer idProductoProveedor) { this.idProductoProveedor = idProductoProveedor; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public Character getEstado() { return estado; }
    public void setEstado(Character estado) { this.estado = estado; }
    public Set<DetalleOrdenCompra> getDetalleOrdenCompras() { return detalleOrdenCompras; }
    public void setDetalleOrdenCompras(Set<DetalleOrdenCompra> detalleOrdenCompras) { this.detalleOrdenCompras = detalleOrdenCompras; }
}