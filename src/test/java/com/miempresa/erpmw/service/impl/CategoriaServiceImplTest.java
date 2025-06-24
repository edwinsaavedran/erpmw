package com.miempresa.erpmw.service.impl;

import com.miempresa.erpmw.model.Categoria;
import com.miempresa.erpmw.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // Habilita el uso de anotaciones de Mockito
class CategoriaServiceImplTest {

    @Mock // Creamos un "mock" o simulación del repositorio. No usará la BD real.
    private CategoriaRepository categoriaRepository;

    @InjectMocks // Creamos una instancia real del servicio e inyectamos el mock de arriba en él.
    private CategoriaServiceImpl categoriaService;

    private Categoria categoria;

    @BeforeEach // Este método se ejecuta antes de cada test
    void setUp() {
        categoria = new Categoria();
        categoria.setIdCategoria(1);
        categoria.setNombre("Laptops");
        categoria.setEstado('A');
    }

    @Test
    @DisplayName("Prueba para guardar una categoría exitosamente")
    void testGuardarCategoria_Success() {
        // Arrange (Preparar)
        // Cuando se llame a findByNombre con cualquier cadena, devolvemos un Optional vacío (simulando que no existe).
        given(categoriaRepository.findByNombre(categoria.getNombre())).willReturn(Optional.empty());
        // Cuando se llame a save, devolvemos el mismo objeto categoría.
        given(categoriaRepository.save(categoria)).willReturn(categoria);

        // Act (Actuar)
        Categoria categoriaGuardada = categoriaService.guardarCategoria(categoria);

        // Assert (Verificar)
        assertThat(categoriaGuardada).isNotNull(); // Verificamos que el resultado no sea nulo.
        assertThat(categoriaGuardada.getNombre()).isEqualTo("Laptops");
    }

    @Test
    @DisplayName("Prueba para lanzar excepción al guardar una categoría con nombre duplicado")
    void testGuardarCategoria_NombreDuplicado() {
        // Arrange
        // Simulamos que ya existe una categoría con el mismo nombre y un ID diferente.
        given(categoriaRepository.findByNombre(categoria.getNombre())).willReturn(Optional.of(categoria));

        // Act & Assert
        // Verificamos que al llamar a guardarCategoria con una nueva categoría (id null) pero nombre existente,
        // se lance una IllegalArgumentException.
        Categoria categoriaDuplicada = new Categoria();
        categoriaDuplicada.setNombre("Laptops");

        assertThrows(IllegalArgumentException.class, () -> {
            categoriaService.guardarCategoria(categoriaDuplicada);
        });

        // Verificamos que el método save NUNCA fue llamado, porque la validación falló antes.
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }
}