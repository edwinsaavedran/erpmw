package com.miempresa.erpmw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "t_marca")
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_marca")
    private Integer idMarca;

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 200)
    @Column(name = "nombre", length = 200, nullable = false, unique = true)
    private String nombre;

    @Column(name = "estado", length = 1, nullable = false)
    private Character estado;

    @OneToMany(mappedBy = "marca", fetch = FetchType.LAZY)
    private Set<Producto> productos;

    // Constructores, Getters y Setters
    public Marca() {}

    public Integer getIdMarca() { return idMarca; }
    public void setIdMarca(Integer idMarca) { this.idMarca = idMarca; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Character getEstado() { return estado; }
    public void setEstado(Character estado) { this.estado = estado; }
    public Set<Producto> getProductos() { return productos; }
    public void setProductos(Set<Producto> productos) { this.productos = productos; }
}