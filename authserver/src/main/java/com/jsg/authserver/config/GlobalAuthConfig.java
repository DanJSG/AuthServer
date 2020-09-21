package com.jsg.authserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.jsg.authserver.auth.AuthorizationInterceptor;

@Configuration
public class GlobalAuthConfig implements WebMvcConfigurer {
	
	@Autowired
	AuthorizationInterceptor authInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {	
		registry.addInterceptor(authInterceptor).addPathPatterns(
				"/api/v1/settings/auth*",
				"/api/v1/app/register*",
				"/api/v1/app/getAll*",
				"/api/v1/app/update*",
				"/api/v1/app/delete*"
		);
	}

}
