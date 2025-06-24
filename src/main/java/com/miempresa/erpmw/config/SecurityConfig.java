package com.miempresa.erpmw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    // 1. Permitir acceso público
                    .requestMatchers( "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                    .requestMatchers("/login", "/", "/acceso-denegado").permitAll()

                    // 2. Proteger URLs de Productos con Permisos Específicos
                    .requestMatchers(HttpMethod.GET, "/productos/nuevo", "/productos/editar/**").hasAuthority("EDITAR_PRODUCTOS") // Usamos un permiso general de edición para crear y editar
                    .requestMatchers(HttpMethod.POST, "/productos/guardar").hasAuthority("EDITAR_PRODUCTOS")
                    .requestMatchers(HttpMethod.GET, "/productos/desactivar/**", "/productos/activar/**").hasAuthority("DESACTIVAR_PRODUCTOS")
                    .requestMatchers(HttpMethod.GET, "/productos", "/productos/").hasAuthority("LEER_PRODUCTOS")
                    
                    // Puedes añadir reglas similares para las otras entidades
                    // .requestMatchers("/categorias/**").hasAuthority("LEER_CATEGORIAS")
                    
                    // 3. Proteger todas las demás URLs (regla general)
                    .anyRequest().authenticated() 
            )
            .formLogin(formLogin ->
                formLogin
                    .loginPage("/login")
                    .defaultSuccessUrl("/", true) // Redirigir a la página de inicio después de login
                    .permitAll()
            )
            .logout(logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
            )
            .exceptionHandling(exceptionHandling ->
                // Configurar una página de acceso denegado personalizada
                exceptionHandling.accessDeniedPage("/acceso-denegado")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}