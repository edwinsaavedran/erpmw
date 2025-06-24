package com.miempresa.erpmw.repository;

import com.miempresa.erpmw.model.Marca; // Importar Marca
import com.miempresa.erpmw.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByEstado(Character estado);

    Optional<Producto> findByNombre(String nombre); // Este podría ya no ser tan útil solo o necesitar contexto

    // Nuevo método para la validación de unicidad combinada
    Optional<Producto> findByNombreAndMarca(String nombre, Marca marca);

    @Query("SELECT p FROM Producto p JOIN FETCH p.marca JOIN FETCH p.categoria WHERE p.idProducto = :id")
    Optional<Producto> findByIdWithMarcaAndCategoria(@Param("id") Integer id);

    @Query("SELECT p FROM Producto p JOIN FETCH p.marca JOIN FETCH p.categoria ORDER BY p.nombre ASC")
    List<Producto> findAllWithMarcaAndCategoria();

    @Query("SELECT p FROM Producto p JOIN FETCH p.marca JOIN FETCH p.categoria WHERE p.estado = :estado ORDER BY p.nombre ASC")
    List<Producto> findByEstadoWithMarcaAndCategoria(@Param("estado") Character estado);
}
