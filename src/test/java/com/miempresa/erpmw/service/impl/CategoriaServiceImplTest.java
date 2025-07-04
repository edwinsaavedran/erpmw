package com.miempresa.erpmw.service.impl;

import com.miempresa.erpmw.model.Categoria;
import com.miempresa.erpmw.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void testObtenerTodasLasCategorias_DebeDevolverListaDeCategorias() {
        // --- 1. Arrange (Organizar) ---
        // Preparamos los datos de prueba y definimos el comportamiento de nuestro mock.
        
        // Creamos algunos objetos de categoría de ejemplo.
        Categoria cat1 = new Categoria("Electrónica", 'A');
        cat1.setIdCategoria(1);
        
        Categoria cat2 = new Categoria("Ropa", 'A');
        cat2.setIdCategoria(2);
        
        List<Categoria> listaDeCategoriasMock = Arrays.asList(cat1, cat2);

        // Le decimos a Mockito: "Cuando alguien llame al método findAll() de categoriaRepository,
        // entonces devuelve nuestra listaDeCategoriasMock".
        Mockito.when(categoriaRepository.findAll()).thenReturn(listaDeCategoriasMock);


        // --- 2. Act (Actuar) ---
        // Ejecutamos el método que queremos probar.
        List<Categoria> resultado = categoriaService.obtenerTodasLasCategorias();


        // --- 3. Assert (Verificar) ---
        // Comprobamos que el resultado sea el esperado.

        // ¿El resultado no es nulo?
        Assertions.assertNotNull(resultado);
        
        // ¿El tamaño de la lista es 2?
        Assertions.assertEquals(2, resultado.size());
        
        // ¿El nombre de la primera categoría es "Electrónica"?
        Assertions.assertEquals("Electrónica", resultado.get(0).getNombre());

        // También podemos verificar que la interacción con el mock ocurrió como esperábamos.
        // ¿Se llamó al método findAll() del repositorio exactamente una vez?
        Mockito.verify(categoriaRepository, Mockito.times(1)).findAll();
    }

    @Test
    void testGuardarCategoria_CuandoNombreYaExiste_DebeLanzarExcepcion() {
        // --- 1. Arrange (Organizar) ---
        // Datos para la nueva categoría que se intenta guardar
        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setNombre("Electrónica");

        // Simulamos que ya existe una categoría con el mismo nombre en la BD
        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setIdCategoria(99); // Un ID diferente al de la nueva
        categoriaExistente.setNombre("Electrónica");
        
        // Entrenamos al mock: Cuando se busque por el nombre "Electrónica",
        // debe devolver la categoría que ya existe.
        Mockito.when(categoriaRepository.findByNombre("Electrónica")).thenReturn(Optional.of(categoriaExistente));

        // --- 2. Act & 3. Assert (Actuar y Verificar) ---
        // Usamos assertThrows para verificar que la llamada al servicio lanza la excepción esperada.
        // El primer argumento es el tipo de excepción que esperamos.
        // El segundo es una lambda que contiene la acción que debe provocar la excepción.
        IllegalArgumentException exceptionLanzada = assertThrows(IllegalArgumentException.class, () -> {
            categoriaService.guardarCategoria(nuevaCategoria);
        });

        // Opcional pero recomendado: Verificamos que el mensaje de la excepción es el correcto.
        String mensajeEsperado = "Ya existe una categoría con el nombre: Electrónica";
        assertEquals(mensajeEsperado, exceptionLanzada.getMessage());

        // Verificamos también que el método save() del repositorio NUNCA fue llamado,
        // porque la lógica debería haber fallado antes.
        Mockito.verify(categoriaRepository, Mockito.never()).save(any(Categoria.class));
    }
}