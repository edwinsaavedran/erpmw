package com.miempresa.erpmw.repository;

import com.miempresa.erpmw.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    List<Categoria> findByEstado(Character estado);

    Optional<Categoria> findByNombre(String nombre);
}
