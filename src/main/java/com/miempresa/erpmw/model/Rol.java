package com.miempresa.erpmw.model;

import jakarta.persistence.*;
import java.util.Set; // Importar Set

@Entity
@Table(name = "t_rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, nullable = false, unique = true)
    private String nombre; // Ej: "ROLE_ADMIN", "ROLE_USER"

    // --- NUEVA RELACIÓN AÑADIDA ---
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "t_rol_permiso",
            joinColumns = @JoinColumn(name = "rol_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permiso_id", referencedColumnName = "id")
    )
    private Set<Permiso> permisos;

    // Constructores, Getters y Setters
    public Rol() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    // Getters y Setters para la nueva relación
    public Set<Permiso> getPermisos() { return permisos; }
    public void setPermisos(Set<Permiso> permisos) { this.permisos = permisos; }
}
