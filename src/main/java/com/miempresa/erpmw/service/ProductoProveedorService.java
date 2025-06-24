package com.miempresa.erpmw.service;

import com.miempresa.erpmw.model.ProductoProveedor;
import java.util.List;
import java.util.Optional;

public interface ProductoProveedorService {
    List<ProductoProveedor> obtenerTodosConDetalles();
    List<ProductoProveedor> obtenerActivosConDetalles();
    Optional<ProductoProveedor> obtenerPorIdConDetalles(Integer id);
    ProductoProveedor guardar(ProductoProveedor productoProveedor) throws IllegalArgumentException;
    ProductoProveedor desactivar(Integer id);
    ProductoProveedor activar(Integer id);
}
