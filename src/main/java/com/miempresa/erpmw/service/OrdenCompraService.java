package com.miempresa.erpmw.service;

import com.miempresa.erpmw.dto.OrdenCompraDTO;
import com.miempresa.erpmw.model.OrdenCompra;
import java.util.List;
import java.util.Optional;

public interface OrdenCompraService {

    /**
     * Crea una nueva orden de compra con sus detalles.
     * @param ordenCompraDTO DTO con los datos de la orden y sus detalles.
     * @return La entidad OrdenCompra persistida.
     * @throws IllegalArgumentException si hay datos inválidos (ej. producto/proveedor no existe o no está activo).
     */
    OrdenCompra crearOrdenCompra(OrdenCompraDTO ordenCompraDTO) throws IllegalArgumentException;

    /**
     * Obtiene todas las órdenes de compra, usualmente ordenadas por fecha descendente.
     * Se recomienda que las entidades relacionadas (como el proveedor principal si existiera o el número de ítems)
     * se carguen eficientemente si se van a mostrar en la lista.
     * @return Lista de todas las órdenes de compra.
     */
    List<OrdenCompra> obtenerTodasLasOrdenes(); // Podríamos necesitar una versión con JOIN FETCH para detalles si la lista lo requiere

    /**
     * Obtiene una orden de compra específica por su ID, incluyendo todos sus detalles
     * y las entidades relacionadas de cada detalle (Producto, Proveedor, Marca, Categoría).
     * @param id El ID de la orden de compra.
     * @return Un Optional conteniendo la OrdenCompra si se encuentra, o vacío si no.
     */
    Optional<OrdenCompra> obtenerOrdenPorIdConDetallesCompletos(Integer id);

    // Futuros métodos podrían incluir:
    // OrdenCompra aprobarOrden(Integer id);
    // OrdenCompra cancelarOrden(Integer id);
    // OrdenCompra marcarComoRecibida(Integer id);
    // List<OrdenCompra> obtenerOrdenesPorEstado(Character estado);
    // List<OrdenCompra> obtenerOrdenesPorProveedor(Integer idProveedor);
    // List<OrdenCompra> obtenerOrdenesPorRangoDeFechas(LocalDateTime inicio, LocalDateTime fin);
}