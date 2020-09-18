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
	public GlobalCorsConfig(@Value("${CORS_ORIGINS}") String[] origins) {
		GlobalCorsConfig.origins = origins;
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/v1/authorize*").allowCredentials(true).allowedOrigins(origins);
		registry.addMapping("/api/v1/revoke*").allowCredentials(true).allowedOrigins(origins);
		registry.addMapping("/api/v1/register*").allowCredentials(true).allowedOrigins(origins);
		registry.addMapping("/api/v1/settings/auth*").allowCredentials(true).allowedOrigins(origins);
		registry.addMapping("/api/v1/app/register*").allowCredentials(true).allowedOrigins(origins);
		registry.addMapping("/api/v1/app/getAll*").allowCredentials(true).allowedOrigins(origins);
		// allow all origins as this endpoint can be accessed from external applications
		registry.addMapping("/api/v1/token*").allowCredentials(true).allowedOrigins("*");
		registry.addMapping("/api/v1/userInfo*").allowCredentials(true).allowedOrigins("*");
	}
	
}
