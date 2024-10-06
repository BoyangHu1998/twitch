package com.boyangh.twitch;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Apply CORS to all paths
                .allowedOrigins("http://localhost",
                                "https://topical-cleanly-condor.ngrok-free.app",
                                "https://twitch-recommend.netlify.app")  // Allow your frontend origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Allowed HTTP methods
                .allowedHeaders("*")  // Allow all headers
                .allowCredentials(true)  // Allow credentials (cookies, etc.)
                .exposedHeaders("Set-Cookie")
                .maxAge(3600);  // Cache the preflight response for 1 hour
    }
}