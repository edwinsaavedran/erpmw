package com.miempresa.erpmw.controller;

import com.miempresa.erpmw.dto.OrdenCompraDTO;
import com.miempresa.erpmw.model.OrdenCompra;
import com.miempresa.erpmw.model.ProductoProveedor; // Para la lista de items
import com.miempresa.erpmw.service.OrdenCompraService;
import com.miempresa.erpmw.service.ProductoProveedorService; // Para obtener items para el formulario
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/ordenes")
public class OrdenCompraController {

    private static final Logger logger = LoggerFactory.getLogger(OrdenCompraController.class);

    private final OrdenCompraService ordenCompraService;
    private final ProductoProveedorService productoProveedorService; // Para la lista de items

    @Autowired
    public OrdenCompraController(OrdenCompraService ordenCompraService,
                                 ProductoProveedorService productoProveedorService) {
        this.ordenCompraService = ordenCompraService;
        this.productoProveedorService = productoProveedorService;
    }

    private void prepararModeloComun(Model model, String titulo) {
        model.addAttribute("titulo", titulo);
        model.addAttribute("paginaActiva", "ordenes"); // Para la navbar
    }

    @GetMapping
    public String listarOrdenes(Model model) {
        logger.info("Accediendo a listarOrdenes");
        prepararModeloComun(model, "Listado de Órdenes de Compra");
        try {
            List<OrdenCompra> ordenes = ordenCompraService.obtenerTodasLasOrdenes();
            model.addAttribute("ordenes", ordenes);
        } catch (Exception e) {
            logger.error("Error al listar órdenes de compra", e);
            model.addAttribute("error_lista", "Error al cargar las órdenes de compra.");
        }
        return "orden/lista"; // Usará el layout
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNuevaOrden(Model model) {
        logger.info("Accediendo a mostrarFormularioNuevaOrden");
        prepararModeloComun(model, "Registrar Nueva Orden de Compra");

        OrdenCompraDTO ordenCompraDTO = new OrdenCompraDTO();
        ordenCompraDTO.setFecha(LocalDateTime.now()); // Fecha por defecto

        // Cargar items disponibles (ProductoProveedor activos) para el <select> en el formulario
        try {
            // Usamos obtenerActivosConDetalles para tener info completa si se quiere mostrar más en el select
            List<ProductoProveedor> itemsDisponibles = productoProveedorService.obtenerActivosConDetalles();
            model.addAttribute("itemsDisponibles", itemsDisponibles);
        } catch (Exception e) {
            logger.error("Error al cargar items disponibles para nueva orden", e);
            model.addAttribute("error_formulario", "No se pudieron cargar los productos/proveedores disponibles.");
            // Considerar si se debe permitir continuar o mostrar un error más bloqueante
        }
        
        model.addAttribute("ordenCompraDTO", ordenCompraDTO);
        return "orden/formulario"; // Usará el layout
    }

    @PostMapping("/guardar")
    public String guardarOrden(@Valid @ModelAttribute("ordenCompraDTO") OrdenCompraDTO ordenCompraDTO,
                               BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Intentando guardar orden de compra: {}", ordenCompraDTO);
        prepararModeloComun(model, "Registrar Nueva Orden de Compra"); // Título si volvemos al form

        if (ordenCompraDTO.getDetalles() == null || ordenCompraDTO.getDetalles().isEmpty()) {
            // Spring @NotEmpty en el DTO debería manejar esto, pero una doble verificación no está de más
            // o si se quiere un mensaje de error más específico que el de la anotación.
            result.reject("NotEmpty.ordenCompraDTO.detalles", "La orden de compra debe tener al menos un detalle.");
        }
        
        // Si hay errores de validación de las anotaciones del DTO
        if (result.hasErrors()) {
            logger.warn("Errores de validación al guardar orden: {}", result.getAllErrors());
            // Recargar itemsDisponibles para que el formulario se muestre correctamente
            List<ProductoProveedor> itemsDisponibles = productoProveedorService.obtenerActivosConDetalles();
            model.addAttribute("itemsDisponibles", itemsDisponibles);
            return "orden/formulario"; // Volver al formulario
        }

        try {
            OrdenCompra ordenGuardada = ordenCompraService.crearOrdenCompra(ordenCompraDTO);
            redirectAttributes.addFlashAttribute("success", "Orden de Compra creada con éxito. ID: " + ordenGuardada.getIdOrdenCompra());
            logger.info("Orden de compra guardada con ID: {}", ordenGuardada.getIdOrdenCompra());
            return "redirect:/ordenes/ver/" + ordenGuardada.getIdOrdenCompra(); // Redirigir a la vista de detalle
        } catch (IllegalArgumentException e) {
            logger.warn("Error de negocio al guardar orden de compra (IllegalArgumentException): {}", e.getMessage());
            model.addAttribute("error_formulario", e.getMessage());
            List<ProductoProveedor> itemsDisponibles = productoProveedorService.obtenerActivosConDetalles();
            model.addAttribute("itemsDisponibles", itemsDisponibles);
            return "orden/formulario"; // Volver al formulario
        } catch (Exception e) {
            logger.error("Error inesperado al guardar orden de compra", e);
            model.addAttribute("error_formulario", "Error inesperado al procesar la orden de compra. Intente nuevamente.");
            List<ProductoProveedor> itemsDisponibles = productoProveedorService.obtenerActivosConDetalles();
            model.addAttribute("itemsDisponibles", itemsDisponibles);
            return "orden/formulario"; // Volver al formulario
        }
    }

    @GetMapping("/ver/{id}")
    public String verOrden(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Accediendo a verOrden con ID: {}", id);
        prepararModeloComun(model, "Detalle de Orden de Compra");
        try {
            Optional<OrdenCompra> ordenOpt = ordenCompraService.obtenerOrdenPorIdConDetallesCompletos(id);
            if (ordenOpt.isPresent()) {
                model.addAttribute("ordenCompra", ordenOpt.get());
                return "orden/ver"; // Usará el layout
            } else {
                logger.warn("Orden de compra no encontrada con ID: {}", id);
                redirectAttributes.addFlashAttribute("error", "Orden de compra no encontrada.");
                return "redirect:/ordenes";
            }
        } catch (Exception e) {
            logger.error("Error al obtener orden de compra con ID " + id, e);
            redirectAttributes.addFlashAttribute("error", "Error al cargar la orden de compra.");
            return "redirect:/ordenes";
        }
    }
    
    // Endpoint API opcional para obtener detalles de un ProductoProveedor (si JS lo necesita)
    // Podría estar en ProductoProveedorController si es más genérico
    @GetMapping("/api/iteminfo/{idProductoProveedor}")
    @ResponseBody // Para que devuelva JSON directamente
    public ProductoProveedor obtenerInfoItem(@PathVariable Integer idProductoProveedor) {
        // Aquí usamos el servicio de ProductoProveedor para obtener el item con todos sus detalles
        // Esto es útil si el select en el formulario solo tiene el ID y necesitas cargar precio, nombre, etc., dinámicamente.
        return productoProveedorService.obtenerPorIdConDetalles(idProductoProveedor)
                .orElse(null); // Manejar el caso de no encontrado en el JS que llama
    }
}