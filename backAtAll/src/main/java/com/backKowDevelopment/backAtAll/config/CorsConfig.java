// src/main/java/.../config/CorsConfig.java
package com.backKowDevelopment.backAtAll.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Si NO envías cookies/sesión, puedes usar allowedOrigins.
                // Si en algún momento usas cookies (withCredentials), usa allowedOriginPatterns y allowCredentials(true)
                .allowedOrigins(
                        "http://localhost:4200",
                        "https://backkowdevelopment.onrender.com"
                )
                // Añade PATCH y HEAD por si los usas ahora o más adelante
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders("*")
                .exposedHeaders("Location", "Content-Disposition")
                .allowCredentials(false)   // cámbialo a true solo si enviarás cookies/sesión desde el front
                .maxAge(3600);
    }
}