package com.jsg.authserver.tokenhandlers;

import java.util.Calendar;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public final class JWTHandler {
	
	private static final long msSecond = 1000;
	
	public static String createToken(long id, String secret, int expirySecs) {
		Algorithm algorithm = Algorithm.HMAC256(secret);
		int hash = Calendar.getInstance().getTime().hashCode();
		System.out.println(hash);
		String jwt = JWT.create()
				.withIssuedAt(new Date())
				.withExpiresAt(new Date((Calendar.getInstance().getTimeInMillis() * msSecond) + expirySecs))
				.withClaim("id", id)
				.withClaim("hash", hash)
				.sign(algorithm);
		return jwt;
	}
	
	public static Boolean verifyToken(String token, String secret) {
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
	
	public static long getIdFromToken(String token) {
		DecodedJWT jwt = JWT.decode(token);
		return jwt.getClaim("id").asLong();
	}
	
	public static Boolean tokenIsValid(String token, String secret) {
		if(token == null || !JWTHandler.verifyToken(token, secret)) {
			return false;
		}
		return true;
	}
	
}
