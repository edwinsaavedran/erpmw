package com.miempresa.erpmw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginForm(Model model, Principal principal, 
                                @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout) {
        
        if (principal != null) {
            // Si el usuario ya está logueado, redirigir a la página de inicio
            return "redirect:/";
        }

        if (error != null) {
            model.addAttribute("error", "Credenciales inválidas. Por favor, intente de nuevo.");
        }

        if (logout != null) {
            model.addAttribute("success", "Has cerrado sesión exitosamente.");
        }

        return "login"; // Devuelve el nombre de la plantilla login.html
    }
}
