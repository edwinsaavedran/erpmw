package com.miempresa.erpmw.controller;

import com.miempresa.erpmw.model.Proveedor;
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
@RequestMapping("/proveedores")
public class ProveedorController {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorController.class);
    private final ProveedorService proveedorService;

    @Autowired
    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    // Método helper para atributos comunes
    private void prepararModeloComun(Model model, String titulo) {
        model.addAttribute("titulo", titulo);
        model.addAttribute("paginaActiva", "proveedores"); // Clave para la navbar
    }

    @GetMapping
    public String listarProveedores(Model model) {
        logger.info("Accediendo a listarProveedores");
        prepararModeloComun(model, "Listado de Proveedores");
        try {
            model.addAttribute("proveedores", proveedorService.obtenerTodosLosProveedores());
        } catch (Exception e) {
            logger.error("Error al listar proveedores", e);
            model.addAttribute("error_lista", "Error al cargar la lista de proveedores.");
        }
        return "proveedor/lista"; // Vista que usará el layout
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoProveedor(Model model) {
        logger.info("Accediendo a mostrarFormularioNuevoProveedor");
        prepararModeloComun(model, "Registrar Nuevo Proveedor");
        model.addAttribute("proveedor", new Proveedor());
        return "proveedor/formulario"; // Vista que usará el layout
    }

    @PostMapping("/guardar")
    public String guardarProveedor(@Valid @ModelAttribute("proveedor") Proveedor proveedor,
                                   BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Intentando guardar proveedor: {}", proveedor);
        String tituloFormKey = (proveedor.getIdProveedor() == null ? "Registrar Nuevo Proveedor" : "Editar Proveedor");
        prepararModeloComun(model, tituloFormKey); // Para recargar el título y paginaActiva si hay error

        if (result.hasErrors()) {
            logger.warn("Errores de validación al guardar proveedor: {}", result.getAllErrors());
            return "proveedor/formulario"; // Vista que usará el layout
        }

        try {
            boolean esCreacion = proveedor.getIdProveedor() == null;
            Proveedor proveedorGuardado = proveedorService.guardarProveedor(proveedor);
            String mensajeExito = esCreacion ? "Proveedor creado con éxito." : "Proveedor actualizado con éxito.";
            redirectAttributes.addFlashAttribute("success", mensajeExito + " ID: " + proveedorGuardado.getIdProveedor());
            logger.info(mensajeExito);
            return "redirect:/proveedores";
        } catch (IllegalArgumentException e) {
            logger.warn("Error al guardar proveedor (IllegalArgumentException): {}", e.getMessage());
            model.addAttribute("error_formulario", e.getMessage());
            return "proveedor/formulario"; // Vista que usará el layout
        } catch (Exception e) {
            logger.error("Error inesperado al guardar proveedor", e);
            model.addAttribute("error_formulario", "Error inesperado al guardar el proveedor.");
            return "proveedor/formulario"; // Vista que usará el layout
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarProveedor(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Accediendo a mostrarFormularioEditarProveedor para ID: {}", id);
        prepararModeloComun(model, "Editar Proveedor");
        Optional<Proveedor> proveedorOpt = proveedorService.obtenerProveedorPorId(id);
        if (proveedorOpt.isPresent()) {
            model.addAttribute("proveedor", proveedorOpt.get());
            return "proveedor/formulario"; // Vista que usará el layout
        } else {
            logger.warn("Proveedor no encontrado para editar con ID: {}", id);
            redirectAttributes.addFlashAttribute("error", "Proveedor no encontrado.");
            return "redirect:/proveedores";
        }
    }

    @GetMapping("/desactivar/{id}")
    public String desactivarProveedor(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        // ... (sin cambios, ya que solo redirige y pasa paginaActiva indirectamente por la redirección) ...
        try {
            proveedorService.desactivarProveedor(id);
            redirectAttributes.addFlashAttribute("warning", "Proveedor desactivado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/proveedores";
    }
    
    @GetMapping("/activar/{id}")
    public String activarProveedor(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        // ... (sin cambios) ...
        try {
            proveedorService.activarProveedor(id);
            redirectAttributes.addFlashAttribute("success", "Proveedor activado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/proveedores";
    }
}