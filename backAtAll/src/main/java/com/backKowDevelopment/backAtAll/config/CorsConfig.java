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
                .allowedOrigins(
                        "http://localhost:4200",
                        "https://kowdevelopment.netlify.app" // ← tu FE en producción
                )
                .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS","HEAD")
                .allowedHeaders("*")
                .exposedHeaders("Location","Content-Disposition")
                .allowCredentials(false)
                .maxAge(3600);
    }
}