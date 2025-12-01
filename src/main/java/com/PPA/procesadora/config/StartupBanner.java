package com.PPA.procesadora.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartupBanner implements ApplicationListener<SpringApplicationEvent> {

    @Override
    public void onApplicationEvent(SpringApplicationEvent event) {
        if (event.getClass().getSimpleName().equals("ApplicationReadyEvent")) {
            printBanner();
        }
    }

    private void printBanner() {
        String banner = """
            
            ╔════════════════════════════════════════════════════════════════╗
            ║                                                                ║
            ║                     PPA PROCESADORA                            ║
            ║            SISTEMA DE CONTROL DE ALMACÉN DINÁMICO              ║
            ║                                                                ║
            ╠════════════════════════════════════════════════════════════════╣
            ║                                                                ║
            ║  ✓  Aplicación iniciada correctamente                          ║
            ║                                                                ║
            ║   ACCEDE A LA APLICACIÓN:                                      ║
            ║                                                                ║
            ║     http://localhost:8089/login                                ║
            ║                                                                ║
            ║   CREDENCIALES DE PRUEBA:                                      ║
            ║     Usuario: admin                                             ║
            ║     Contraseña: sk50501n                                       ║
            ║                                                                ║
            ║    INFORMACIÓN DEL SERVIDOR:                                   ║
            ║     Puerto: 8089                                               ║
            ║     Contexto: /                                                ║
            ║     BD: almacen_db (MySQL puerto 3310)                         ║
            ║                                                                ║
            ║   ENDPOINTS:                                                   ║
            ║     API REST:   http://localhost:8089/api                      ║
            ║     Login:      http://localhost:8089/login                    ║
            ║     Dashboard:  http://localhost:8089/dashboard                ║
            ║                                                                ║
            ║                                                                ║
            ╚════════════════════════════════════════════════════════════════╝
            """;

        System.out.println(banner);
    }
}