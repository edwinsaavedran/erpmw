package com.miempresa.erpmw.repository;

import com.miempresa.erpmw.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Método crucial que usará nuestro UserDetailsService para encontrar un usuario por su nombre de usuario
    Optional<Usuario> findByUsername(String username);
}
