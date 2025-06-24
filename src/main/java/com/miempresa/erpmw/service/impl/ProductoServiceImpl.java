package com.miempresa.erpmw.service.impl;

import com.miempresa.erpmw.model.Categoria;
import com.miempresa.erpmw.model.Marca;
import com.miempresa.erpmw.model.Producto;
import com.miempresa.erpmw.repository.CategoriaRepository;
import com.miempresa.erpmw.repository.MarcaRepository;
import com.miempresa.erpmw.repository.ProductoRepository;
import com.miempresa.erpmw.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final MarcaRepository marcaRepository;
    private final CategoriaRepository categoriaRepository;

    @Autowired
    public ProductoServiceImpl(ProductoRepository productoRepository,
                               MarcaRepository marcaRepository,
                               CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.marcaRepository = marcaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // ... (otros métodos obtenerTodosLosProductosConDetalles, etc. sin cambios) ...
    @Override
    @Transactional(readOnly = true)
    public List<Producto> obtenerTodosLosProductosConDetalles() {
        return productoRepository.findAllWithMarcaAndCategoria();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosActivosConDetalles() {
        return productoRepository.findByEstadoWithMarcaAndCategoria('A');
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> obtenerProductoPorIdConDetalles(Integer id) {
        return productoRepository.findByIdWithMarcaAndCategoria(id);
    }


    @Override
    @Transactional
    public Producto guardarProducto(Producto producto) throws IllegalArgumentException {
        // Asegurar que la marca y categoría referenciadas existan y estén activas primero
        if (producto.getMarca() == null || producto.getMarca().getIdMarca() == null) {
            throw new IllegalArgumentException("Debe seleccionar una marca válida para el producto.");
        }
        Marca marca = marcaRepository.findById(producto.getMarca().getIdMarca())
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada con ID: " + producto.getMarca().getIdMarca()));
        if (marca.getEstado() != 'A') {
             throw new IllegalArgumentException("La marca seleccionada '" + marca.getNombre() + "' no está activa.");
        }
        producto.setMarca(marca); // Asignar la entidad completa y gestionada

        if (producto.getCategoria() == null || producto.getCategoria().getIdCategoria() == null) {
            throw new IllegalArgumentException("Debe seleccionar una categoría válida para el producto.");
        }
        Categoria categoria = categoriaRepository.findById(producto.getCategoria().getIdCategoria())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + producto.getCategoria().getIdCategoria()));
         if (categoria.getEstado() != 'A') {
             throw new IllegalArgumentException("La categoría seleccionada '" + categoria.getNombre() + "' no está activa.");
        }
        producto.setCategoria(categoria); // Asignar la entidad completa y gestionada

        // Validar unicidad de la combinación nombre + marca
        Optional<Producto> existenteConMismoNombreYMarca = productoRepository.findByNombreAndMarca(producto.getNombre(), producto.getMarca());
        if (existenteConMismoNombreYMarca.isPresent() &&
            (producto.getIdProducto() == null || !existenteConMismoNombreYMarca.get().getIdProducto().equals(producto.getIdProducto()))) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre '" + producto.getNombre() + "' para la marca '" + producto.getMarca().getNombre() + "'.");
        }

        if (producto.getIdProducto() == null) { // Nuevo producto
            producto.setEstado('A');
        }
        return productoRepository.save(producto);
    }

    // ... (métodos cambiarEstadoProducto, desactivarProducto, activarProducto sin cambios) ...
    private Producto cambiarEstadoProducto(Integer id, Character nuevoEstado, String accion) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id + " para " + accion + "."));
        producto.setEstado(nuevoEstado);
        return productoRepository.save(producto);
    }

    @Override
    @Transactional
    public Producto desactivarProducto(Integer id) {
        return cambiarEstadoProducto(id, 'I', "desactivar");
    }

    @Override
    @Transactional
    public Producto activarProducto(Integer id) {
        return cambiarEstadoProducto(id, 'A', "activar");
    }
}
