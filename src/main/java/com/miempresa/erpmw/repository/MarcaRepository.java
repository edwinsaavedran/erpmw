package com.miempresa.erpmw.repository;

import com.miempresa.erpmw.model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Integer> {

    List<Marca> findByEstado(Character estado);

    Optional<Marca> findByNombre(String nombre);
}