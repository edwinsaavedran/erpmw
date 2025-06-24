package com.miempresa.erpmw.controller;

import com.miempresa.erpmw.model.ProductoProveedor;
import com.miempresa.erpmw.service.ProductoProveedorService;
import com.miempresa.erpmw.service.ProductoService;
import com.miempresa.erpmw.service.ProveedorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/productoproveedor")
public class ProductoProveedorController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoProveedorController.class);
    private final ProductoProveedorService productoProveedorService;
    private final ProductoService productoService;
    private final ProveedorService proveedorService;

    @Autowired
    public ProductoProveedorController(ProductoProveedorService productoProveedorService,
                                       ProductoService productoService,
                                       ProveedorService proveedorService) {
        this.productoProveedorService = productoProveedorService;
        this.productoService = productoService;
        this.proveedorService = proveedorService;
    }

    // Método helper para atributos comunes y listas desplegables del formulario
    private void prepararModeloFormulario(Model model, String titulo) {
        model.addAttribute("titulo", titulo);
        model.addAttribute("paginaActiva", "productoproveedor"); // Clave para la navbar
        model.addAttribute("listaProductos", productoService.obtenerProductosActivosConDetalles());
        model.addAttribute("listaProveedores", proveedorService.obtenerProveedoresActivos());
    }

    // Método helper para atributos comunes de la página de listado
    private void prepararModeloLista(Model model, String titulo) {
        model.addAttribute("titulo", titulo);
        model.addAttribute("paginaActiva", "productoproveedor");
    }

    @GetMapping
    public String listar(Model model) {
        logger.info("Accediendo a listar ProductoProveedor");
        prepararModeloLista(model, "Listado de Productos por Proveedor");
        try {
            model.addAttribute("registros", productoProveedorService.obtenerTodosConDetalles());
        } catch (Exception e) {
            logger.error("Error al listar ProductoProveedor", e);
            model.addAttribute("error_lista", "Error al cargar los registros.");
        }
        return "productoproveedor/lista"; // Vista que usará el layout
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        logger.info("Accediendo a mostrarFormularioNuevo ProductoProveedor");
        prepararModeloFormulario(model, "Asignar Producto a Proveedor");
        model.addAttribute("productoProveedor", new ProductoProveedor());
        return "productoproveedor/formulario"; // Vista que usará el layout
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("productoProveedor") ProductoProveedor productoProveedor,
                          BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Intentando guardar ProductoProveedor: {}", productoProveedor);
        String tituloFormKey = (productoProveedor.getIdProductoProveedor() == null ? "Asignar Producto a Proveedor" : "Editar Asignación");
        prepararModeloFormulario(model, tituloFormKey); // Para recargar título, paginaActiva y listas si hay error

        if (result.hasErrors()) {
            logger.warn("Errores de validación al guardar ProductoProveedor: {}", result.getAllErrors());
            return "productoproveedor/formulario"; // Vista que usará el layout
        }

        try {
            boolean esCreacion = productoProveedor.getIdProductoProveedor() == null;
            ProductoProveedor guardado = productoProveedorService.guardar(productoProveedor);
            String mensajeExito = esCreacion ? "Asignación creada con éxito." : "Asignación actualizada con éxito.";
            redirectAttributes.addFlashAttribute("success", mensajeExito + " ID: " + guardado.getIdProductoProveedor());
            logger.info(mensajeExito);
            return "redirect:/productoproveedor";
        } catch (IllegalArgumentException e) {
            logger.warn("Error al guardar ProductoProveedor (IllegalArgumentException): {}", e.getMessage());
            model.addAttribute("error_formulario", e.getMessage());
            return "productoproveedor/formulario"; // Vista que usará el layout
        } catch (Exception e) {
            logger.error("Error inesperado al guardar ProductoProveedor", e);
            model.addAttribute("error_formulario", "Error inesperado al guardar la asignación.");
            return "productoproveedor/formulario"; // Vista que usará el layout
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Accediendo a mostrarFormularioEditar ProductoProveedor para ID: {}", id);
        prepararModeloFormulario(model, "Editar Asignación Producto-Proveedor");
        Optional<ProductoProveedor> opt = productoProveedorService.obtenerPorIdConDetalles(id);
        if (opt.isPresent()) {
            model.addAttribute("productoProveedor", opt.get());
            return "productoproveedor/formulario"; // Vista que usará el layout
        } else {
            logger.warn("Asignación Producto-Proveedor no encontrada para editar con ID: {}", id);
            redirectAttributes.addFlashAttribute("error", "Asignación no encontrada.");
            return "redirect:/productoproveedor";
        }
    }

    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            productoProveedorService.desactivar(id);
            redirectAttributes.addFlashAttribute("warning", "Asignación desactivada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/productoproveedor";
    }

    @GetMapping("/activar/{id}")
    public String activar(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            productoProveedorService.activar(id);
            redirectAttributes.addFlashAttribute("success", "Asignación activada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/productoproveedor";
    }
}