package com.miempresa.erpmw.repository;

import com.miempresa.erpmw.model.Producto;
import com.miempresa.erpmw.model.ProductoProveedor;
import com.miempresa.erpmw.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoProveedorRepository extends JpaRepository<ProductoProveedor, Integer> {

    // Para validar unicidad de la combinación Producto y Proveedor
    Optional<ProductoProveedor> findByProductoAndProveedor(Producto producto, Proveedor proveedor);

    // Para cargar eficientemente los detalles para las listas
    @Query("SELECT pp FROM ProductoProveedor pp JOIN FETCH pp.producto p JOIN FETCH p.marca JOIN FETCH p.categoria JOIN FETCH pp.proveedor ORDER BY p.nombre ASC, pp.proveedor.nombre ASC")
    List<ProductoProveedor> findAllWithDetails();

    @Query("SELECT pp FROM ProductoProveedor pp JOIN FETCH pp.producto p JOIN FETCH p.marca JOIN FETCH p.categoria JOIN FETCH pp.proveedor WHERE pp.idProductoProveedor = :id")
    Optional<ProductoProveedor> findByIdWithDetails(@Param("id") Integer id);
    
    @Query("SELECT pp FROM ProductoProveedor pp JOIN FETCH pp.producto p JOIN FETCH p.marca JOIN FETCH p.categoria JOIN FETCH pp.proveedor WHERE pp.estado = :estado ORDER BY p.nombre ASC, pp.proveedor.nombre ASC")
    List<ProductoProveedor> findByEstadoWithDetails(@Param("estado") Character estado);

}
