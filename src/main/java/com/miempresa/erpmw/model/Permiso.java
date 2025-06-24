package com.miempresa.erpmw.model;

import jakarta.persistence.*;

@Entity
@Table(name = "t_permiso")
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false, unique = true)
    private String nombre; // Ej: "PRODUCTO_LEER", "PRODUCTO_CREAR", "ORDEN_APROBAR"

    // Constructores, Getters y Setters
    public Permiso() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}