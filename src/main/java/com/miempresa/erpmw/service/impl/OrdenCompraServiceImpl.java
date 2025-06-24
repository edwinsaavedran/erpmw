package com.miempresa.erpmw.service.impl;

import com.miempresa.erpmw.dto.DetalleOrdenCompraDTO;
import com.miempresa.erpmw.dto.OrdenCompraDTO;
import com.miempresa.erpmw.model.DetalleOrdenCompra;
import com.miempresa.erpmw.model.OrdenCompra;
import com.miempresa.erpmw.model.ProductoProveedor;
import com.miempresa.erpmw.repository.OrdenCompraRepository;
import com.miempresa.erpmw.repository.ProductoProveedorRepository;
// DetalleOrdenCompraRepository es opcional si confiamos en la cascada, pero puede ser útil.
// import com.example.erp.compras.repository.DetalleOrdenCompraRepository;
import com.miempresa.erpmw.service.OrdenCompraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenCompraServiceImpl implements OrdenCompraService {

    private static final Logger logger = LoggerFactory.getLogger(OrdenCompraServiceImpl.class);

    private final OrdenCompraRepository ordenCompraRepository;
    private final ProductoProveedorRepository productoProveedorRepository;
    // private final DetalleOrdenCompraRepository detalleOrdenCompraRepository; // Opcional

    @Autowired
    public OrdenCompraServiceImpl(OrdenCompraRepository ordenCompraRepository,
                                  ProductoProveedorRepository productoProveedorRepository) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.productoProveedorRepository = productoProveedorRepository;
    }

    @Override
    @Transactional
    public OrdenCompra crearOrdenCompra(OrdenCompraDTO ordenCompraDTO) throws IllegalArgumentException {
        logger.info("Iniciando creación de orden de compra");

        OrdenCompra ordenCompra = new OrdenCompra();
        ordenCompra.setFecha(ordenCompraDTO.getFecha() != null ? ordenCompraDTO.getFecha() : LocalDateTime.now());
        ordenCompra.setTipoPago(ordenCompraDTO.getTipoPago());
        ordenCompra.setMoneda(ordenCompraDTO.getMoneda());
        ordenCompra.setObservaciones(ordenCompraDTO.getObservaciones());
        ordenCompra.setEstado('P'); // Pendiente por defecto

        BigDecimal totalGeneralOrden = BigDecimal.ZERO;

        if (ordenCompraDTO.getDetalles() == null || ordenCompraDTO.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La orden de compra debe contener al menos un ítem.");
        }

        for (DetalleOrdenCompraDTO detalleDTO : ordenCompraDTO.getDetalles()) {
            if (detalleDTO.getIdProductoProveedor() == null || detalleDTO.getCantidad() == null || detalleDTO.getCantidad() <= 0) {
                throw new IllegalArgumentException("Cada detalle de la orden debe tener un producto-proveedor y una cantidad válida.");
            }

            // Usamos findById para obtener el ProductoProveedor. findByIdWithDetails no es necesario aquí porque
            // solo necesitamos el precio y validar su estado. Los detalles completos del producto/proveedor
            // no son cruciales para la lógica de creación de la orden en sí, pero sí para la selección en el formulario.
            ProductoProveedor productoProveedor = productoProveedorRepository.findById(detalleDTO.getIdProductoProveedor())
                    .orElseThrow(() -> new IllegalArgumentException("El ítem Producto-Proveedor con ID " + detalleDTO.getIdProductoProveedor() + " no existe."));

            if (productoProveedor.getEstado() != 'A') {
                throw new IllegalArgumentException("El ítem '" + productoProveedor.getProducto().getNombre() +
                        " (Proveedor: " + productoProveedor.getProveedor().getNombre() + ")' no está activo.");
            }
            
            // (Opcional) Validación de stock
            // if (productoProveedor.getStock() < detalleDTO.getCantidad()) {
            //     throw new IllegalArgumentException("Stock insuficiente para el producto: " + productoProveedor.getProducto().getNombre() +
            //                                        " (Proveedor: " + productoProveedor.getProveedor().getNombre() + "). Stock actual: " + productoProveedor.getStock());
            // }


            DetalleOrdenCompra detalleEntidad = new DetalleOrdenCompra();
            detalleEntidad.setProductoProveedor(productoProveedor);
            detalleEntidad.setCantidad(detalleDTO.getCantidad());

            BigDecimal precioUnitario = productoProveedor.getPrecioUnitario();
            if (precioUnitario == null) {
                throw new IllegalArgumentException("El ítem '" + productoProveedor.getProducto().getNombre() +
                        " (Proveedor: " + productoProveedor.getProveedor().getNombre() + ")' no tiene un precio unitario definido.");
            }
            BigDecimal subtotalDetalle = precioUnitario.multiply(BigDecimal.valueOf(detalleDTO.getCantidad()));
            detalleEntidad.setSubtotal(subtotalDetalle);
            detalleEntidad.setEstado('P'); // Estado del detalle, podría ser el mismo de la orden o gestionarse independientemente

            ordenCompra.addDetalle(detalleEntidad); // Establece la relación bidireccional
            totalGeneralOrden = totalGeneralOrden.add(subtotalDetalle);
        }

        ordenCompra.setTotal(totalGeneralOrden);
        
        OrdenCompra ordenGuardada = ordenCompraRepository.save(ordenCompra);
        logger.info("Orden de compra creada con ID: {}", ordenGuardada.getIdOrdenCompra());

        // (Opcional) Lógica para descontar stock después de guardar la orden
        // for(DetalleOrdenCompra detalle : ordenGuardada.getDetalles()) {
        //     ProductoProveedor pp = detalle.getProductoProveedor();
        //     pp.setStock(pp.getStock() - detalle.getCantidad());
        //     productoProveedorRepository.save(pp);
        // }

        return ordenGuardada;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompra> obtenerTodasLasOrdenes() {
        // Por defecto, las devuelve tal cual. Podríamos querer ordenar o usar un DTO si la lista es compleja.
        // Para una lista simple, esto puede ser suficiente y los detalles se cargarían LAZY si se acceden.
        // Si la vista de lista necesita info de detalles (ej. número de ítems), se necesitaría una consulta más específica o DTO.
        return ordenCompraRepository.findAllByOrderByFechaDesc(); // Asume que tienes este método en el repo
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdenCompra> obtenerOrdenPorIdConDetallesCompletos(Integer id) {
        // El repositorio debería tener un método que haga JOIN FETCH de los detalles y sus entidades anidadas.
        return ordenCompraRepository.findByIdAndFetchDetallesCompletos(id); // Asume que tienes este método en el repo
    }
}