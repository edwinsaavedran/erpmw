package com.miempresa.erpmw.service.impl;

import com.miempresa.erpmw.model.Permiso; // Importar Permiso
import com.miempresa.erpmw.model.Rol;
import com.miempresa.erpmw.model.Usuario;
import com.miempresa.erpmw.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream; // Importar Stream

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Buscar el usuario en nuestra base de datos (sin cambios aquí)
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado con ese username: " + username));
        
        // 2. Obtener las autoridades (roles + permisos). Esta llamada usa el método actualizado.
        Collection<? extends GrantedAuthority> authorities = mapRolesAndPermissionsToAuthorities(usuario.getRoles());

        // 3. Devolver el objeto UserDetails con las autoridades completas (sin cambios aquí)
        return new User(usuario.getUsername(), usuario.getPassword(), usuario.isEnabled(), true, true, true, authorities);
    }

    /**
     * Mapea los roles Y los permisos de un usuario a una colección de GrantedAuthority.
     * @param roles El conjunto de roles del usuario.
     * @return una colección de GrantedAuthority que incluye tanto los nombres de los roles como los de los permisos.
     */
    private Collection<? extends GrantedAuthority> mapRolesAndPermissionsToAuthorities(Set<Rol> roles) {
        // 1. Mapear los roles a GrantedAuthority (ej. "ROLE_ADMIN")
        Stream<GrantedAuthority> roleAuthorities = roles.stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre()));

        // 2. Mapear los permisos de cada rol a GrantedAuthority (ej. "PRODUCTO_LEER")
        Stream<GrantedAuthority> permissionAuthorities = roles.stream()
                .flatMap(rol -> rol.getPermisos().stream()) // Usamos flatMap para aplanar la lista de listas de permisos
                .map(permiso -> new SimpleGrantedAuthority(permiso.getNombre()));

        // 3. Combinar ambos streams en una sola lista
        return Stream.concat(roleAuthorities, permissionAuthorities)
                .collect(Collectors.toSet()); // Usamos toSet() para evitar duplicados si un permiso estuviera en varios roles
    }
}
