package com.miempresa.erpmw.service;

import com.miempresa.erpmw.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> obtenerTodosLosProductosConDetalles();
    List<Producto> obtenerProductosActivosConDetalles();
    Optional<Producto> obtenerProductoPorIdConDetalles(Integer id);
    Producto guardarProducto(Producto producto) throws IllegalArgumentException;
    Producto desactivarProducto(Integer id);
    Producto activarProducto(Integer id);
}
