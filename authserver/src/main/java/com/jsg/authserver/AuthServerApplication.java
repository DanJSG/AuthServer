package com.jsg.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class AuthServerApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(AuthServerApplication.class, args);
		System.out.println("JSG Auth Server started...");
	}
}
