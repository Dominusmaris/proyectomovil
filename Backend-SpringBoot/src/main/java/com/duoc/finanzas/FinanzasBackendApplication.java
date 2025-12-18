package com.duoc.finanzas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * APLICACIÓN PRINCIPAL - BACKEND FINANZAS
 * Proyecto: App Finanzas DuocUC - DSY1105
 * Estudiante: [Tu nombre]
 */
@SpringBootApplication(scanBasePackages = {
    "com.duoc.finanzas",
    "com.registrofinanzas.backend"
})
public class FinanzasBackendApplication {

    private static final Logger logger = LoggerFactory.getLogger(FinanzasBackendApplication.class);

    public static void main(String[] args) {
        logger.info("🚀 Iniciando Backend Finanzas - Proyecto DuocUC");
        logger.info("📱 App Android conectándose al backend...");

        SpringApplication.run(FinanzasBackendApplication.class, args);

        logger.info("✅ Backend Finanzas iniciado correctamente");
        logger.info("🌐 Endpoints disponibles en /api/");
    }
}