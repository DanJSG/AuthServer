package com.jsg.authserver.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class AuthToken {
	
	private String token;
	private long id;
	private String name;
	
	public AuthToken(String token) {
		this.token = token;
		id = extractId();
		name = extractName();
	}
	
	public String getToken() {
		return token;
	}
	
	public String getName() {
		return name;
	}
	
	public long getId() {
		return id;
	}
	
	public boolean verify(String secret) {
		Algorithm algorithm = Algorithm.HMAC256(secret);
		JWTVerifier verifier = JWT.require(algorithm).build();
		try {
			verifier.verify(token);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private long extractId() {
		DecodedJWT jwt = JWT.decode(token);
		return jwt.getClaim("id").asLong();
	}
	
	private String extractName() {
		DecodedJWT jwt = JWT.decode(token);
		return jwt.getClaim("name").asString();
	}
	
}
