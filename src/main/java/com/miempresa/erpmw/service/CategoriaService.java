package com.miempresa.erpmw.service;

import com.miempresa.erpmw.model.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaService {
    List<Categoria> obtenerTodasLasCategorias();
    List<Categoria> obtenerCategoriasActivas();
    Optional<Categoria> obtenerCategoriaPorId(Integer id);
    Categoria guardarCategoria(Categoria categoria) throws IllegalArgumentException;
    Categoria desactivarCategoria(Integer id);
    Categoria activarCategoria(Integer id);
}
