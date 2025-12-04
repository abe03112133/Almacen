package com.PPA.procesadora.config;

import com.PPA.procesadora.entity.Rol;
import com.PPA.procesadora.entity.Usuario;
import com.PPA.procesadora.repository.RolRepository;
import com.PPA.procesadora.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        initializeDatabase();
        initializeAdminUser();
    }

    private void initializeDatabase() {
        try {
            // Verificar si la base de datos está vacía chequeando si existen roles
            long rolesCount = rolRepository.count();

            if (rolesCount == 0) {
                log.info("Base de datos vacía detectada. Inicializando datos...");

                // Ejecutar el primer script (inserts básicos y procedimiento)
                executeScript("db/01-inserts-basicos.sql");
                log.info("✓ Datos básicos insertados");

                // Ejecutar el segundo script (creación de racks y espacios)
                executeScript("db/02-crear-racks.sql");
                log.info("✓ Racks y espacios creados");

                log.info("Inicialización de base de datos completada exitosamente");
            } else {
                log.info("Base de datos ya contiene datos. Omitiendo inicialización.");
            }
        } catch (Exception e) {
            log.error("Error al inicializar base de datos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al inicializar base de datos", e);
        }
    }

    private void executeScript(String scriptPath) {
        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource(scriptPath));
            populator.setSeparator("$$"); // Para manejar los DELIMITER
            populator.execute(dataSource);
        } catch (Exception e) {
            log.error("Error ejecutando script {}: {}", scriptPath, e.getMessage());
            throw new RuntimeException("Error ejecutando script: " + scriptPath, e);
        }
    }

    private void initializeAdminUser() {
        String adminUsername = "ADMIN";

        if (!usuarioRepository.existsByUsername(adminUsername)) {
            log.info("Usuario ADMIN no encontrado. Creando usuario ADMIN...");

            // Buscar el rol ADMIN por tipo
            Rol rolAdmin = rolRepository.findByTipo("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado en la base de datos"));

            // Crear usuario ADMIN
            Usuario admin = new Usuario();
            admin.setUsername(adminUsername);
            admin.setNombreCompleto("Administrador del Sistema");
            admin.setPasswordHash(passwordEncoder.encode("admin123")); // Cambia esta contraseña
            admin.setRol(rolAdmin);
            admin.setActivo(true);
            admin.setFechaIngreso(LocalDateTime.now());

            usuarioRepository.save(admin);
            log.info("Usuario ADMIN creado exitosamente");
        } else {
            log.info("Usuario ADMIN ya existe en la base de datos");
        }
    }
}