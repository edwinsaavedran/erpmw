package com.miempresa.erpmw.service;

import com.miempresa.erpmw.model.Proveedor;
import java.util.List;
import java.util.Optional;

public interface ProveedorService {
    List<Proveedor> obtenerTodosLosProveedores();
    List<Proveedor> obtenerProveedoresActivos();
    Optional<Proveedor> obtenerProveedorPorId(Integer id);
    Proveedor guardarProveedor(Proveedor proveedor) throws IllegalArgumentException;
    Proveedor desactivarProveedor(Integer id);
    Proveedor activarProveedor(Integer id);
}
