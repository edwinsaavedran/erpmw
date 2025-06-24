package com.miempresa.erpmw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "t_producto",
       uniqueConstraints = { // Define la restricción de unicidad para la combinación de columnas
           @UniqueConstraint(columnNames = {"nombre", "fk_marca"}, name = "uk_producto_nombre_marca")
       }
)
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @NotBlank(message = "El nombre del producto no puede estar vacío.")
    @Size(max = 200, message = "El nombre no puede exceder los 200 caracteres.")
    @Column(name = "nombre", length = 200, nullable = false) // Se quita unique = true de aquí
    private String nombre;

    @NotNull(message = "Debe seleccionar una marca.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_marca", referencedColumnName = "id_marca", nullable = false)
    private Marca marca;

    @NotNull(message = "Debe seleccionar una categoría.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_categoria", referencedColumnName = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "estado", length = 1, nullable = false)
    private Character estado;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private Set<ProductoProveedor> productoProveedores;

    // Constructores, Getters y Setters (sin cambios aquí, asegúrate que estén completos)
    public Producto() {}

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Marca getMarca() { return marca; }
    public void setMarca(Marca marca) { this.marca = marca; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Character getEstado() { return estado; }
    public void setEstado(Character estado) { this.estado = estado; }
    public Set<ProductoProveedor> getProductoProveedores() { return productoProveedores; }
    public void setProductoProveedores(Set<ProductoProveedor> productoProveedores) { this.productoProveedores = productoProveedores; }
}

