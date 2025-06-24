package com.miempresa.erpmw.config;

import com.miempresa.erpmw.model.Permiso;
import com.miempresa.erpmw.model.Rol;
import com.miempresa.erpmw.model.Usuario;
import com.miempresa.erpmw.repository.PermisoRepository;
import com.miempresa.erpmw.repository.RolRepository;
import com.miempresa.erpmw.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // Importar

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                      PermisoRepository permisoRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional // <--- AÑADIR ESTA ANOTACIÓN
    public void run(String... args) throws Exception {
        if (rolRepository.count() > 0 || usuarioRepository.count() > 0 || permisoRepository.count() > 0) {
            System.out.println("La base de datos ya contiene datos de seguridad, no se cargaron datos iniciales.");
            return; // Salir si ya hay datos
        }

        System.out.println("Cargando datos iniciales (permisos, roles y usuarios)...");

        // --- 1. Crear todos los Permisos ---
        // Se crean los objetos primero
        Set<Permiso> todosLosPermisos = Arrays.stream(new String[]{
            "LEER_CATEGORIAS", "CREAR_CATEGORIAS", "EDITAR_CATEGORIAS", "DESACTIVAR_CATEGORIAS",
            "LEER_MARCAS", "CREAR_MARCAS", "EDITAR_MARCAS", "DESACTIVAR_MARCAS",
            "LEER_PRODUCTOS", "CREAR_PRODUCTOS", "EDITAR_PRODUCTOS", "DESACTIVAR_PRODUCTOS",
            "LEER_PROVEEDORES", "CREAR_PROVEEDORES", "EDITAR_PROVEEDORES", "DESACTIVAR_PROVEEDORES",
            "LEER_ORDENES", "CREAR_ORDENES", "APROBAR_ORDENES", "CANCELAR_ORDENES"
        }).map(nombre -> {
            Permiso p = new Permiso();
            p.setNombre(nombre);
            return p;
        }).collect(Collectors.toSet());
        // Se guardan todos juntos
        permisoRepository.saveAll(todosLosPermisos);

        // --- 2. Crear los Roles y asignarles Permisos ---
        Set<Permiso> permisosUser = todosLosPermisos.stream()
            .filter(p -> p.getNombre().startsWith("LEER_") || p.getNombre().equals("CREAR_ORDENES"))
            .collect(Collectors.toSet());

        Rol rolAdmin = new Rol();
        rolAdmin.setNombre("ROLE_ADMIN");
        rolAdmin.setPermisos(todosLosPermisos);

        Rol rolUser = new Rol();
        rolUser.setNombre("ROLE_USER");
        rolUser.setPermisos(permisosUser);
        
        // Guardar ambos roles
        rolRepository.saveAll(List.of(rolAdmin, rolUser));

        // --- 3. Crear los Usuarios y asignarles Roles ---
        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("12345"));
        admin.setEnabled(true);
        admin.setRoles(Set.of(rolAdmin));

        Usuario user = new Usuario();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("123"));
        user.setEnabled(true);
        user.setRoles(Set.of(rolUser));
        
        // Guardar ambos usuarios
        usuarioRepository.saveAll(List.of(admin, user));

        System.out.println("Datos iniciales cargados exitosamente.");
    }
}
