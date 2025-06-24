package com.miempresa.erpmw.service.impl;

import com.miempresa.erpmw.model.Proveedor;
import com.miempresa.erpmw.repository.ProveedorRepository;
import com.miempresa.erpmw.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // Para StringUtils.hasText

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    @Autowired
    public ProveedorServiceImpl(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proveedor> obtenerTodosLosProveedores() {
        return proveedorRepository.findAll(); // Considera ordenar
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proveedor> obtenerProveedoresActivos() {
        return proveedorRepository.findByEstado('A');
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Proveedor> obtenerProveedorPorId(Integer id) {
        return proveedorRepository.findById(id);
    }

    @Override
    @Transactional
    public Proveedor guardarProveedor(Proveedor proveedor) throws IllegalArgumentException {
        // Validar RUC único si se proporciona
        if (StringUtils.hasText(proveedor.getRuc())) {
            Optional<Proveedor> existenteConMismoRuc = proveedorRepository.findByRuc(proveedor.getRuc());
            if (existenteConMismoRuc.isPresent() &&
                (proveedor.getIdProveedor() == null || !existenteConMismoRuc.get().getIdProveedor().equals(proveedor.getIdProveedor()))) {
                throw new IllegalArgumentException("Ya existe un proveedor con el RUC: " + proveedor.getRuc());
            }
        }

        // Validar Email único si se proporciona
        if (StringUtils.hasText(proveedor.getEmail())) {
            Optional<Proveedor> existenteConMismoEmail = proveedorRepository.findByEmail(proveedor.getEmail());
            if (existenteConMismoEmail.isPresent() &&
                (proveedor.getIdProveedor() == null || !existenteConMismoEmail.get().getIdProveedor().equals(proveedor.getIdProveedor()))) {
                throw new IllegalArgumentException("Ya existe un proveedor con el email: " + proveedor.getEmail());
            }
        }
        
        // Podrías añadir una validación para nombre único si es un requisito de negocio, similar a RUC y Email.
        // Optional<Proveedor> existenteConMismoNombre = proveedorRepository.findByNombre(proveedor.getNombre());
        // if (existenteConMismoNombre.isPresent() && /* misma lógica de ID */ ) {
        //     throw new IllegalArgumentException("Ya existe un proveedor con el nombre: " + proveedor.getNombre());
        // }


        if (proveedor.getIdProveedor() == null) { // Nuevo proveedor
            proveedor.setEstado('A');
        }
        return proveedorRepository.save(proveedor);
    }

    private Proveedor cambiarEstadoProveedor(Integer id, Character nuevoEstado, String accion) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado con ID: " + id + " para " + accion + "."));
        // Aquí podrías añadir lógica de negocio, ej. no desactivar si tiene órdenes de compra pendientes/activas.
        proveedor.setEstado(nuevoEstado);
        return proveedorRepository.save(proveedor);
    }

    @Override
    @Transactional
    public Proveedor desactivarProveedor(Integer id) {
        return cambiarEstadoProveedor(id, 'I', "desactivar");
    }

    @Override
    @Transactional
    public Proveedor activarProveedor(Integer id) {
        return cambiarEstadoProveedor(id, 'A', "activar");
    }
}