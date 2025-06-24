package com.miempresa.erpmw.controller;

import com.miempresa.erpmw.model.Categoria;
import com.miempresa.erpmw.service.CategoriaService;
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
@RequestMapping("/categorias")
public class CategoriaController {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaController.class);
    private final CategoriaService categoriaService;

    @Autowired
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    private void prepararModeloComun(Model model, String titulo, String paginaActiva) {
        model.addAttribute("titulo", titulo);
        model.addAttribute("paginaActiva", paginaActiva);
    }

    @GetMapping
    public String listarCategorias(Model model) {
        logger.info("Accediendo a listarCategorias");
        prepararModeloComun(model, "Listado de Categorías", "categorias");
        try {
            model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
        } catch (Exception e) {
            logger.error("Error al listar categorías", e);
            model.addAttribute("error_lista", "Error al cargar la lista de categorías.");
        }
        return "categoria/lista"; // Vista que usará el layout
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevaCategoria(Model model) {
        logger.info("Accediendo a mostrarFormularioNuevaCategoria");
        prepararModeloComun(model, "Registrar Nueva Categoría", "categorias");
        model.addAttribute("categoria", new Categoria());
        return "categoria/formulario"; // Vista que usará el layout
    }

    @PostMapping("/guardar")
    public String guardarCategoria(@Valid @ModelAttribute("categoria") Categoria categoria,
                                   BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Intentando guardar categoría: {}", categoria);
        String tituloFormKey = categoria.getIdCategoria() == null ? "Registrar Nueva Categoría" : "Editar Categoría";
        prepararModeloComun(model, tituloFormKey, "categorias");

        if (result.hasErrors()) {
            logger.warn("Errores de validación al guardar categoría: {}", result.getAllErrors());
            return "categoria/formulario"; // Vista que usará el layout
        }

        try {
            boolean esCreacion = categoria.getIdCategoria() == null; // Determinar antes de guardar
            Categoria categoriaGuardada = categoriaService.guardarCategoria(categoria);
            String mensajeExito = esCreacion ? "Categoría creada con éxito." : "Categoría actualizada con éxito.";
            redirectAttributes.addFlashAttribute("success", mensajeExito + " ID: " + categoriaGuardada.getIdCategoria());
            logger.info(mensajeExito);
            return "redirect:/categorias";
        } catch (IllegalArgumentException e) {
            logger.warn("Error al guardar categoría (IllegalArgumentException): {}", e.getMessage());
            model.addAttribute("error_formulario", e.getMessage());
            return "categoria/formulario"; // Vista que usará el layout
        } catch (Exception e) {
            logger.error("Error inesperado al guardar categoría", e);
            model.addAttribute("error_formulario", "Error inesperado al guardar la categoría.");
            return "categoria/formulario"; // Vista que usará el layout
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarCategoria(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Accediendo a mostrarFormularioEditarCategoria para ID: {}", id);
        prepararModeloComun(model, "Editar Categoría", "categorias");
        Optional<Categoria> categoriaOpt = categoriaService.obtenerCategoriaPorId(id);
        if (categoriaOpt.isPresent()) {
            model.addAttribute("categoria", categoriaOpt.get());
            return "categoria/formulario"; // Vista que usará el layout
        } else {
            logger.warn("Categoría no encontrada para editar con ID: {}", id);
            redirectAttributes.addFlashAttribute("error", "Categoría no encontrada.");
            return "redirect:/categorias";
        }
    }

    @GetMapping("/desactivar/{id}")
    public String desactivarCategoria(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        // ... (sin cambios, ya que solo redirige) ...
        try {
            categoriaService.desactivarCategoria(id);
            redirectAttributes.addFlashAttribute("warning", "Categoría desactivada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categorias";
    }
    
    @GetMapping("/activar/{id}")
    public String activarCategoria(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        // ... (sin cambios, ya que solo redirige) ...
        try {
            categoriaService.activarCategoria(id);
            redirectAttributes.addFlashAttribute("success", "Categoría activada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categorias";
    }
}