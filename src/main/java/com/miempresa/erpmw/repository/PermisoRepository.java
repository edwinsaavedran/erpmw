package com.miempresa.erpmw.repository;

import com.miempresa.erpmw.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Integer> {
    // No necesitamos métodos personalizados por ahora, JpaRepository es suficiente
}
