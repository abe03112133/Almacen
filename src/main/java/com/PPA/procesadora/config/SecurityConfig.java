package com.PPA.procesadora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize

                        // Archivos públicos (necesarios para el login)
                        .requestMatchers(
                                "/static/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/fonts/**"
                        ).permitAll()

                        // Login es público
                        .requestMatchers("/login", "/login/**").permitAll()

                        // Error handler
                        .requestMatchers("/error/**").permitAll()

                        // ========== VISTAS HTML ==========

                        // Almacén (ALMACENAR_TARIMA): ACOMODADOR, DESCANSERO, SURTIDOR, CALIDAD, SUPERVISOR, GERENTE, ADMIN
                        .requestMatchers("/almacen/**").hasAnyRole("ACOMODADOR", "DESCANSERO", "SURTIDOR", "CALIDAD", "SUPERVISOR", "GERENTE", "ADMIN")

                        // Surtido (SURTIR_TARIMA): SURTIDOR, CALIDAD, SUPERVISOR, GERENTE, ADMIN
                        .requestMatchers("/surtidos/**").hasAnyRole("SURTIDOR", "CALIDAD", "SUPERVISOR", "GERENTE", "ADMIN")

                        // Insumos (GESTIONAR_INSUMOS): SUPERVISOR, GERENTE, ADMIN
                        .requestMatchers("/insumos/**").hasAnyRole("SUPERVISOR", "GERENTE", "ADMIN")

                        // Dashboard (todos los usuarios autenticados)
                        .requestMatchers("/dashboard", "/dashboard/**").authenticated()

                        // Producciones (GESTIONAR_PRODUCCIONES): SUPERVISOR, GERENTE, ADMIN
                        .requestMatchers("/producciones/**").hasAnyRole("SUPERVISOR", "GERENTE", "ADMIN")

                        // Productos (GESTIONAR_PRODUCTOS): SUPERVISOR, GERENTE, ADMIN
                        .requestMatchers("/productos/**").hasAnyRole("SUPERVISOR", "GERENTE", "ADMIN")

                        // Racks (GESTIONAR_RACKS): ADMIN
                        .requestMatchers("/racks/**").hasAnyRole("ADMIN")

                        // Usuarios (GESTIONAR_USUARIOS): GERENTE, ADMIN
                        .requestMatchers("/usuarios/**").hasAnyRole("GERENTE", "ADMIN")

                        // ========== APIs REST ==========

                        // Tarimas (ALMACENAR_TARIMA): ACOMODADOR, DESCANSERO, SURTIDOR, CALIDAD, SUPERVISOR, GERENTE, ADMIN
                        .requestMatchers("/api/tarimas/**").hasAnyRole("ACOMODADOR", "DESCANSERO", "SURTIDOR", "CALIDAD", "SUPERVISOR", "GERENTE", "ADMIN")

                        // Surtido (SURTIR_TARIMA): SURTIDOR, CALIDAD, SUPERVISOR, GERENTE, ADMIN
                        .requestMatchers("/api/ordenes-surtido/**").hasAnyRole("SURTIDOR", "CALIDAD", "SUPERVISOR", "GERENTE", "ADMIN")

                        // Insumos (GESTIONAR_INSUMOS): SUPERVISOR, GERENTE, ADMIN
                        .requestMatchers("/api/insumos/**").hasAnyRole("SUPERVISOR", "GERENTE", "ADMIN")

                        // Producciones (GESTIONAR_PRODUCCIONES): SUPERVISOR, GERENTE, ADMIN
                        .requestMatchers("/api/produccion-alm/**").hasAnyRole("SUPERVISOR", "GERENTE", "ADMIN")

                        // Destinos (para Surtido): SURTIDOR, CALIDAD, SUPERVISOR, GERENTE, ADMIN
                        .requestMatchers("/api/destino/**").hasAnyRole("SURTIDOR", "CALIDAD", "SUPERVISOR", "GERENTE", "ADMIN")

                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )

                // Configuración de formulario de login
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // Configuración de logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}