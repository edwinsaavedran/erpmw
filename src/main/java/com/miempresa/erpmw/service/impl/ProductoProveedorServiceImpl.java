package com.miempresa.erpmw.service.impl;

import com.miempresa.erpmw.model.Producto;
import com.miempresa.erpmw.model.ProductoProveedor;
import com.miempresa.erpmw.model.Proveedor;
import com.miempresa.erpmw.repository.ProductoProveedorRepository;
import com.miempresa.erpmw.repository.ProductoRepository;
import com.miempresa.erpmw.repository.ProveedorRepository;
import com.miempresa.erpmw.service.ProductoProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoProveedorServiceImpl implements ProductoProveedorService {

    private final ProductoProveedorRepository productoProveedorRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;

    @Autowired
    public ProductoProveedorServiceImpl(ProductoProveedorRepository productoProveedorRepository,
                                        ProductoRepository productoRepository,
                                        ProveedorRepository proveedorRepository) {
        this.productoProveedorRepository = productoProveedorRepository;
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoProveedor> obtenerTodosConDetalles() {
        return productoProveedorRepository.findAllWithDetails();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoProveedor> obtenerActivosConDetalles() {
        return productoProveedorRepository.findByEstadoWithDetails('A');
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductoProveedor> obtenerPorIdConDetalles(Integer id) {
        return productoProveedorRepository.findByIdWithDetails(id);
    }

    @Override
    @Transactional
    public ProductoProveedor guardar(ProductoProveedor productoProveedor) throws IllegalArgumentException {
        // Validar y cargar Producto
        if (productoProveedor.getProducto() == null || productoProveedor.getProducto().getIdProducto() == null) {
            throw new IllegalArgumentException("Debe seleccionar un producto válido.");
        }
        Producto producto = productoRepository.findById(productoProveedor.getProducto().getIdProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + productoProveedor.getProducto().getIdProducto()));
        if (producto.getEstado() != 'A') {
            throw new IllegalArgumentException("El producto seleccionado '" + producto.getNombre() + "' no está activo.");
        }
        productoProveedor.setProducto(producto); // Asignar entidad gestionada

        // Validar y cargar Proveedor
        if (productoProveedor.getProveedor() == null || productoProveedor.getProveedor().getIdProveedor() == null) {
            throw new IllegalArgumentException("Debe seleccionar un proveedor válido.");
        }
        Proveedor proveedor = proveedorRepository.findById(productoProveedor.getProveedor().getIdProveedor())
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado con ID: " + productoProveedor.getProveedor().getIdProveedor()));
        if (proveedor.getEstado() != 'A') {
            throw new IllegalArgumentException("El proveedor seleccionado '" + proveedor.getNombre() + "' no está activo.");
        }
        productoProveedor.setProveedor(proveedor); // Asignar entidad gestionada

        // Validar unicidad de la combinación Producto-Proveedor
        Optional<ProductoProveedor> existente = productoProveedorRepository.findByProductoAndProveedor(
                productoProveedor.getProducto(), productoProveedor.getProveedor());
        if (existente.isPresent() &&
            (productoProveedor.getIdProductoProveedor() == null || 
             !existente.get().getIdProductoProveedor().equals(productoProveedor.getIdProductoProveedor()))) {
            throw new IllegalArgumentException("Esta combinación de producto y proveedor ya existe.");
        }

        if (productoProveedor.getIdProductoProveedor() == null) { // Nuevo
            productoProveedor.setEstado('A');
        }
        return productoProveedorRepository.save(productoProveedor);
    }

    private ProductoProveedor cambiarEstado(Integer id, Character nuevoEstado, String accion) {
        ProductoProveedor pp = productoProveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro Producto-Proveedor no encontrado con ID: " + id + " para " + accion + "."));
        pp.setEstado(nuevoEstado);
        return productoProveedorRepository.save(pp);
    }

    @Override
    @Transactional
    public ProductoProveedor desactivar(Integer id) {
        return cambiarEstado(id, 'I', "desactivar");
    }

    @Override
    @Transactional
    public ProductoProveedor activar(Integer id) {
        return cambiarEstado(id, 'A', "activar");
    }
}
