package com.miempresa.erpmw.service.impl;

import com.miempresa.erpmw.model.Categoria;
import com.miempresa.erpmw.repository.CategoriaRepository;
import com.miempresa.erpmw.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Autowired
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll(); // Puedes ordenar por nombre si quieres, ej. Sort.by("nombre")
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> obtenerCategoriasActivas() {
        return categoriaRepository.findByEstado('A');
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> obtenerCategoriaPorId(Integer id) {
        return categoriaRepository.findById(id);
    }

    @Override
    @Transactional
    public Categoria guardarCategoria(Categoria categoria) throws IllegalArgumentException {
        Optional<Categoria> existenteConMismoNombre = categoriaRepository.findByNombre(categoria.getNombre());
        if (existenteConMismoNombre.isPresent() && 
            (categoria.getIdCategoria() == null || !existenteConMismoNombre.get().getIdCategoria().equals(categoria.getIdCategoria()))) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }

        if (categoria.getIdCategoria() == null) { // Nueva categoría
            categoria.setEstado('A'); 
        }
        return categoriaRepository.save(categoria);
    }

    private Categoria cambiarEstadoCategoria(Integer id, Character nuevoEstado, String accion) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id + " para " + accion + "."));
        categoria.setEstado(nuevoEstado);
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public Categoria desactivarCategoria(Integer id) {
        return cambiarEstadoCategoria(id, 'I', "desactivar");
    }
    
    @Override
    @Transactional
    public Categoria activarCategoria(Integer id) {
        return cambiarEstadoCategoria(id, 'A', "activar");
    }
}
