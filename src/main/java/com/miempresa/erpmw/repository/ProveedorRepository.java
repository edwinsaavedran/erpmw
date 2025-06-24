package com.miempresa.erpmw.repository;

import com.miempresa.erpmw.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    List<Proveedor> findByEstado(Character estado);

    Optional<Proveedor> findByNombre(String nombre); // Para validación de unicidad de nombre si se requiere

    Optional<Proveedor> findByRuc(String ruc); // Para validar RUC único

    Optional<Proveedor> findByEmail(String email); // Para validar email único
}