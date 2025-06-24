package com.miempresa.erpmw.service.impl;

import com.miempresa.erpmw.model.Marca;
import com.miempresa.erpmw.repository.MarcaRepository;
import com.miempresa.erpmw.service.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MarcaServiceImpl implements MarcaService {

    private final MarcaRepository marcaRepository;

    @Autowired
    public MarcaServiceImpl(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Marca> obtenerTodasLasMarcas() {
        return marcaRepository.findAll(); // Considera ordenar: Sort.by("nombre")
    }

    @Override
    @Transactional(readOnly = true)
    public List<Marca> obtenerMarcasActivas() {
        return marcaRepository.findByEstado('A');
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Marca> obtenerMarcaPorId(Integer id) {
        return marcaRepository.findById(id);
    }

    @Override
    @Transactional
    public Marca guardarMarca(Marca marca) throws IllegalArgumentException {
        Optional<Marca> existenteConMismoNombre = marcaRepository.findByNombre(marca.getNombre());
        if (existenteConMismoNombre.isPresent() &&
            (marca.getIdMarca() == null || !existenteConMismoNombre.get().getIdMarca().equals(marca.getIdMarca()))) {
            throw new IllegalArgumentException("Ya existe una marca con el nombre: " + marca.getNombre());
        }

        if (marca.getIdMarca() == null) { // Nueva marca
            marca.setEstado('A');
        }
        return marcaRepository.save(marca);
    }

    private Marca cambiarEstadoMarca(Integer id, Character nuevoEstado, String accion) {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada con ID: " + id + " para " + accion + "."));
        marca.setEstado(nuevoEstado);
        return marcaRepository.save(marca);
    }

    @Override
    @Transactional
    public Marca desactivarMarca(Integer id) {
        return cambiarEstadoMarca(id, 'I', "desactivar");
    }

    @Override
    @Transactional
    public Marca activarMarca(Integer id) {
        return cambiarEstadoMarca(id, 'A', "activar");
    }
}
