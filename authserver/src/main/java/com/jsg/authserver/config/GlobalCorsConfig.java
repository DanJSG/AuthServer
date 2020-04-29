package com.jsg.authserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer { 
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/auth/authorize*").allowCredentials(true).allowedOrigins("http://local.courier.net:3000");
		registry.addMapping("/api/auth/token*").allowCredentials(true).allowedOrigins("http://local.courier.net:3000");
		registry.addMapping("/api/auth/revoke*").allowCredentials(true).allowedOrigins("http://local.courier.net:3000");
	}
	
}
