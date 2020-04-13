package com.jsg.authserver.datatypes;

import java.util.Map;

import org.bson.internal.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LoginCredentials {
	
	private String email;
	private String password;
	
	@SuppressWarnings("unchecked")
	public LoginCredentials(Map<String, String> body) {
		try {
			Map<String, String> credentialsMap = new ObjectMapper().readValue(new String(Base64.decode(body.get("credentials"))), Map.class);
			email = credentialsMap.get("email");
			password = credentialsMap.get("password");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPassword() {
		return password;
	}
	
}
