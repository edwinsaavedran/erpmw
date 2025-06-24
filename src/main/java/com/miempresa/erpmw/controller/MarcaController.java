package com.miempresa.erpmw.controller;

import com.miempresa.erpmw.model.Marca;
import com.miempresa.erpmw.service.MarcaService;
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
@RequestMapping("/marcas")
public class MarcaController {

    private static final Logger logger = LoggerFactory.getLogger(MarcaController.class);
    private final MarcaService marcaService;

    @Autowired
    public MarcaController(MarcaService marcaService) {
        this.marcaService = marcaService;
    }

    // Método helper para añadir atributos comunes al modelo
    private void prepararModeloComun(Model model, String titulo) {
        model.addAttribute("titulo", titulo);
        model.addAttribute("paginaActiva", "marcas"); // Clave para la navbar
    }

    @GetMapping
    public String listarMarcas(Model model) {
        logger.info("Accediendo a listarMarcas");
        prepararModeloComun(model, "Listado de Marcas");
        try {
            model.addAttribute("marcas", marcaService.obtenerTodasLasMarcas());
        } catch (Exception e) {
            logger.error("Error al listar marcas", e);
            model.addAttribute("error_lista", "Error al cargar la lista de marcas.");
        }
        return "marca/lista"; // Vista que usará el layout
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevaMarca(Model model) {
        logger.info("Accediendo a mostrarFormularioNuevaMarca");
        prepararModeloComun(model, "Registrar Nueva Marca");
        model.addAttribute("marca", new Marca());
        return "marca/formulario"; // Vista que usará el layout
    }

    @PostMapping("/guardar")
    public String guardarMarca(@Valid @ModelAttribute("marca") Marca marca,
                               BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Intentando guardar marca: {}", marca);
        String tituloFormKey = marca.getIdMarca() == null ? "Registrar Nueva Marca" : "Editar Marca";
        prepararModeloComun(model, tituloFormKey); // Preparar modelo por si hay errores y se vuelve al form

        if (result.hasErrors()) {
            logger.warn("Errores de validación al guardar marca: {}", result.getAllErrors());
            return "marca/formulario"; // Vista que usará el layout
        }

        try {
            boolean esCreacion = marca.getIdMarca() == null;
            Marca marcaGuardada = marcaService.guardarMarca(marca);
            String mensajeExito = esCreacion ? "Marca creada con éxito." : "Marca actualizada con éxito.";
            redirectAttributes.addFlashAttribute("success", mensajeExito + " ID: " + marcaGuardada.getIdMarca());
            logger.info(mensajeExito);
            return "redirect:/marcas";
        } catch (IllegalArgumentException e) {
            logger.warn("Error al guardar marca (IllegalArgumentException): {}", e.getMessage());
            model.addAttribute("error_formulario", e.getMessage());
            return "marca/formulario"; // Vista que usará el layout
        } catch (Exception e) {
            logger.error("Error inesperado al guardar marca", e);
            model.addAttribute("error_formulario", "Error inesperado al guardar la marca.");
            return "marca/formulario"; // Vista que usará el layout
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarMarca(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Accediendo a mostrarFormularioEditarMarca para ID: {}", id);
        prepararModeloComun(model, "Editar Marca");
        Optional<Marca> marcaOpt = marcaService.obtenerMarcaPorId(id);
        if (marcaOpt.isPresent()) {
            model.addAttribute("marca", marcaOpt.get());
            return "marca/formulario"; // Vista que usará el layout
        } else {
            logger.warn("Marca no encontrada para editar con ID: {}", id);
            redirectAttributes.addFlashAttribute("error", "Marca no encontrada.");
            return "redirect:/marcas";
        }
    }

    @GetMapping("/desactivar/{id}")
    public String desactivarMarca(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        logger.info("Intentando desactivar marca con ID: {}", id);
        try {
            marcaService.desactivarMarca(id);
            redirectAttributes.addFlashAttribute("warning", "Marca desactivada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Error al desactivar marca con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/marcas";
    }
    
    @GetMapping("/activar/{id}")
    public String activarMarca(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        logger.info("Intentando activar marca con ID: {}", id);
        try {
            marcaService.activarMarca(id);
            redirectAttributes.addFlashAttribute("success", "Marca activada correctamente.");
        } catch (IllegalArgumentException e) {
            logger.warn("Error al activar marca con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/marcas";
    }
}