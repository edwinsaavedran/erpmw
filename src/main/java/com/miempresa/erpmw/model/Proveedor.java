package com.miempresa.erpmw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "t_proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer idProveedor;

    @NotBlank(message = "El nombre del proveedor no puede estar vacío.")
    @Size(max = 120, message = "El nombre no puede exceder los 120 caracteres.")
    @Column(name = "nombre", length = 120, nullable = false)
    private String nombre;

    @Size(max = 20, message = "El RUC no puede exceder los 20 caracteres.")
    @Column(name = "ruc", length = 20, unique = true) // RUC es único
    private String ruc;

    @Size(max = 200, message = "La dirección no puede exceder los 200 caracteres.")
    @Column(name = "direccion", length = 200)
    private String direccion;

    @Email(message = "Debe ser una dirección de correo electrónico válida.")
    @Size(max = 80, message = "El email no puede exceder los 80 caracteres.")
    @Column(name = "email", length = 80, unique = true) // Email también suele ser único
    private String email;

    @Column(name = "estado", length = 1, nullable = false)
    private Character estado; // 'A': Activo, 'I': Inactivo

    @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY)
    private Set<ProductoProveedor> productoProveedores;

    // Constructores, Getters y Setters
    public Proveedor() {}

    public Integer getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Integer idProveedor) { this.idProveedor = idProveedor; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Character getEstado() { return estado; }
    public void setEstado(Character estado) { this.estado = estado; }
    public Set<ProductoProveedor> getProductoProveedores() { return productoProveedores; }
    public void setProductoProveedores(Set<ProductoProveedor> productoProveedores) { this.productoProveedores = productoProveedores; }
}