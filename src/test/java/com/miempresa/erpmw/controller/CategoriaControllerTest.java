package com.miempresa.erpmw.controller;

import com.miempresa.erpmw.model.Categoria;
import com.miempresa.erpmw.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest // Carga el contexto completo de la aplicación Spring Boot
@AutoConfigureMockMvc // Configura MockMvc para hacer peticiones HTTP simuladas
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc; // Objeto para simular peticiones HTTP

    @Autowired
    private CategoriaService categoriaService; // Podemos inyectar servicios para preparar datos si es necesario

    @Test
    @WithMockUser // Simula que un usuario está autenticado. ¡Esencial para nuestras rutas protegidas!
    void testListarCategorias() throws Exception {
        // Arrange: Crear una categoría de prueba para que la lista no esté vacía
        Categoria categoriaDePrueba = new Categoria();
        categoriaDePrueba.setNombre("Test Categoria Lista");
        categoriaService.guardarCategoria(categoriaDePrueba);

        // Act & Assert
        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk()) // Esperamos que la respuesta sea un HTTP 200 OK
                .andExpect(view().name("categoria/lista")) // Esperamos que se renderice la vista correcta
                .andExpect(model().attributeExists("categorias")) // Esperamos que el modelo contenga el atributo "categorias"
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Test Categoria Lista"))); // Verificamos que el nombre de nuestra categoría aparezca en el HTML
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simula un usuario con rol de ADMIN para probar acceso
    void testGuardarNuevaCategoria() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/categorias/guardar")
                    .param("nombre", "Nueva Categoria Test") // Simulamos los parámetros de un formulario
                    .with(csrf())) // Añadimos un token CSRF válido, requerido por Spring Security
                .andExpect(status().is3xxRedirection()) // Esperamos una redirección (HTTP 3xx)
                .andExpect(redirectedUrl("/categorias")); // Esperamos que redirija a la lista
    }
}