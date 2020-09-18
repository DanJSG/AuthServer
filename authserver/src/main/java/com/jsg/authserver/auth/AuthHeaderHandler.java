package com.jsg.authserver.auth;

public final class AuthHeaderHandler {
	
	public static String getBearerToken(String authHeader) {
		if(authHeader == null) {
			return null;
		}
		String[] headerParts = authHeader.split("Bearer");
		if(headerParts.length != 2) {
			return null;
		}
		return headerParts[1].trim();
	}
	
}
