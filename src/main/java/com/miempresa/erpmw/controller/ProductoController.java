package com.miempresa.erpmw.controller;

import com.miempresa.erpmw.model.Producto;
import com.miempresa.erpmw.service.CategoriaService;
import com.miempresa.erpmw.service.MarcaService;
import com.miempresa.erpmw.service.ProductoService;
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
@RequestMapping("/productos")
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);
    private final ProductoService productoService;
    private final MarcaService marcaService;
    private final CategoriaService categoriaService;

    @Autowired
    public ProductoController(ProductoService productoService, MarcaService marcaService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.marcaService = marcaService;
        this.categoriaService = categoriaService;
    }

    // Método helper para atributos comunes y listas desplegables del formulario
    private void prepararModeloFormulario(Model model, String titulo) {
        model.addAttribute("titulo", titulo);
        model.addAttribute("paginaActiva", "productos"); // Clave para la navbar
        model.addAttribute("listaMarcas", marcaService.obtenerMarcasActivas());
        model.addAttribute("listaCategorias", categoriaService.obtenerCategoriasActivas());
    }
    
    // Método helper para atributos comunes de la página de listado
    private void prepararModeloLista(Model model, String titulo) {
        model.addAttribute("titulo", titulo);
        model.addAttribute("paginaActiva", "productos");
    }


    @GetMapping
    public String listarProductos(Model model) {
        logger.info("Accediendo a listarProductos");
        prepararModeloLista(model, "Listado de Productos");
        try {
            model.addAttribute("productos", productoService.obtenerTodosLosProductosConDetalles());
        } catch (Exception e) {
            logger.error("Error al listar productos", e);
            model.addAttribute("error_lista", "Error al cargar la lista de productos.");
        }
        return "producto/lista"; // Vista que usará el layout
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoProducto(Model model) {
        logger.info("Accediendo a mostrarFormularioNuevoProducto");
        prepararModeloFormulario(model, "Registrar Nuevo Producto");
        model.addAttribute("producto", new Producto());
        return "producto/formulario"; // Vista que usará el layout
    }

    @PostMapping("/guardar")
    public String guardarProducto(@Valid @ModelAttribute("producto") Producto producto,
                                  BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Intentando guardar producto: {}", producto);
        String tituloFormKey = (producto.getIdProducto() == null ? "Registrar Nuevo Producto" : "Editar Producto");
        // Preparamos el modelo por si hay errores y necesitamos volver al formulario
        prepararModeloFormulario(model, tituloFormKey); 

        if (result.hasErrors()) {
            logger.warn("Errores de validación al guardar producto: {}", result.getAllErrors());
            return "producto/formulario"; // Vista que usará el layout
        }

        try {
            boolean esCreacion = producto.getIdProducto() == null;
            Producto productoGuardado = productoService.guardarProducto(producto);
            String mensajeExito = esCreacion ? "Producto creado con éxito." : "Producto actualizado con éxito.";
            redirectAttributes.addFlashAttribute("success", mensajeExito + " ID: " + productoGuardado.getIdProducto());
            logger.info(mensajeExito);
            return "redirect:/productos";
        } catch (IllegalArgumentException e) {
            logger.warn("Error al guardar producto (IllegalArgumentException): {}", e.getMessage());
            model.addAttribute("error_formulario", e.getMessage());
            return "producto/formulario"; // Vista que usará el layout
        } catch (Exception e) {
            logger.error("Error inesperado al guardar producto", e);
            model.addAttribute("error_formulario", "Error inesperado al guardar el producto.");
            return "producto/formulario"; // Vista que usará el layout
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarProducto(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Accediendo a mostrarFormularioEditarProducto para ID: {}", id);
        prepararModeloFormulario(model, "Editar Producto");
        Optional<Producto> productoOpt = productoService.obtenerProductoPorIdConDetalles(id);
        if (productoOpt.isPresent()) {
            model.addAttribute("producto", productoOpt.get());
            return "producto/formulario"; // Vista que usará el layout
        } else {
            logger.warn("Producto no encontrado para editar con ID: {}", id);
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
            return "redirect:/productos";
        }
    }

    @GetMapping("/desactivar/{id}")
    public String desactivarProducto(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        logger.info("Intentando desactivar producto con ID: {}", id);
        try {
            productoService.desactivarProducto(id);
            redirectAttributes.addFlashAttribute("warning", "Producto desactivado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Error al desactivar producto con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/productos";
    }

    @GetMapping("/activar/{id}")
    public String activarProducto(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        logger.info("Intentando activar producto con ID: {}", id);
        try {
            productoService.activarProducto(id);
            redirectAttributes.addFlashAttribute("success", "Producto activado correctamente.");
        } catch (IllegalArgumentException e) {
            logger.warn("Error al activar producto con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/productos";
    }
}