package com.miempresa.erpmw.repository;

import com.miempresa.erpmw.model.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Integer> {

    List<OrdenCompra> findAllByOrderByFechaDesc();

    // Carga la OrdenCompra y sus detalles, y de cada detalle, el ProductoProveedor con su Producto y Proveedor.
    // Y del Producto, su Marca y Categoría.
    @Query("SELECT oc FROM OrdenCompra oc " +
           "LEFT JOIN FETCH oc.detalles d " +
           "LEFT JOIN FETCH d.productoProveedor pp " +
           "LEFT JOIN FETCH pp.producto p " +
           "LEFT JOIN FETCH p.marca " +
           "LEFT JOIN FETCH p.categoria " +
           "LEFT JOIN FETCH pp.proveedor " +
           "WHERE oc.idOrdenCompra = :id")
    Optional<OrdenCompra> findByIdAndFetchDetallesCompletos(@Param("id") Integer id);
    
    // Podrías añadir un método similar para obtener todas las órdenes con al menos el primer nivel de detalles si es necesario para la lista.
    // Ejemplo:
    // @Query("SELECT DISTINCT oc FROM OrdenCompra oc LEFT JOIN FETCH oc.detalles ORDER BY oc.fecha DESC")
    // List<OrdenCompra> findAllWithDetallesOrderByFechaDesc();
    // Pero para la lista general, `findAllByOrderByFechaDesc()` podría ser suficiente si no muestras info de detalles allí.
}