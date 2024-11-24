package com.example.meettify.config.connect;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "do2867lf6anbu.cloudfront.net")
                .allowedHeaders("Authorization", "Content-Type")
                .allowCredentials(true)
                .exposedHeaders("Cache-Control", "Content-Type", "X-Accel-Buffering")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
