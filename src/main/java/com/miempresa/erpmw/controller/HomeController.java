package com.miempresa.erpmw.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public String home(Model model) {
        logger.info("Accediendo a la página de inicio");
        model.addAttribute("titulo", "Bienvenido al Sistema de Compras");
        model.addAttribute("paginaActiva", "inicio"); // Para la navbar
        return "home"; // Nombre de la plantilla Thymeleaf (home.html)
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado(Model model) {
        logger.warn("Un usuario intentó acceder a una página sin los permisos necesarios.");
        model.addAttribute("titulo", "Acceso Denegado");
        // No es necesario 'paginaActiva' aquí ya que es una página de error,
        // pero podrías poner "error" si quisieras un estilo específico.
        return "error/403"; // Apunta a la plantilla 403.html dentro de una carpeta 'error'
    }
}