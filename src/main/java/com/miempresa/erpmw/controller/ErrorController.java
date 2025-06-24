package com.miempresa.erpmw.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);
    
    // Aquí puedes implementar métodos para manejar errores globales
    // por ejemplo, manejar excepciones específicas o retornar mensajes personalizados

    // Ejemplo de método para manejar un error 404
    public String handleNotFound() {
        return "Error 404: Recurso no encontrado";
    }

    // Ejemplo de método para manejar un error genérico
    public String handleGenericError() {
        return "Error: Ocurrió un problema inesperado";
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
