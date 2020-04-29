package com.jsg.authserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer { 
	
	private static String[] origins;
	
	@Autowired
	public GlobalCorsConfig(@Value("${cors.origins}") String[] origins) {
		GlobalCorsConfig.origins = origins;
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/auth/authorize*").allowCredentials(true).allowedOrigins(origins);
		registry.addMapping("/api/auth/token*").allowCredentials(true).allowedOrigins(origins);
		registry.addMapping("/api/auth/revoke*").allowCredentials(true).allowedOrigins(origins);
	}
	
}
