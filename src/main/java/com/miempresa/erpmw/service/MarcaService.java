package com.miempresa.erpmw.service;

import com.miempresa.erpmw.model.Marca;
import java.util.List;
import java.util.Optional;

public interface MarcaService {
    List<Marca> obtenerTodasLasMarcas();
    List<Marca> obtenerMarcasActivas();
    Optional<Marca> obtenerMarcaPorId(Integer id);
    Marca guardarMarca(Marca marca) throws IllegalArgumentException;
    Marca desactivarMarca(Integer id);
    Marca activarMarca(Integer id);
}
