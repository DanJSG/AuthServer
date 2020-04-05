package com.jsg.authserver.tokenhandlers;

import java.util.Calendar;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public abstract class JWTHandler {
	
//	private static final String secret = 
//			"%Rw_-C8!tDMtAn3^^KZh7N&m6V+3jnQLmh5b55Xv%WZTS?DbQH_"
//			+ "Sgt+HLecK8$LXtTxbQZP+Fp@jgeAzeU*D9Yv$*bS66V2bct!X"
//			+ "Cq7t=Lm6#d@grZB5eAX$GbJFmbbG#FfJvBqAQ@JBH=NJnfpk5"
//			+ "XfVnq^7jDNmtM8^$%2dxPv^?Jb$B6MuP?BQ=4Mm596_%-=fmm"
//			+ "AAZd56kMp@^C6^r?TjCkG4V8CeRV@z5=mFNbjaGDJMVUFPPHA"
//			+ "wua8*A_BD";
	
	protected static String secret;
	
	private static final Algorithm algorithm = Algorithm.HMAC256(secret);
	
	private static final long msMinute = 60000;
	
	public static String createToken(long id) {
		String jwt = JWT.create()
				.withIssuedAt(new Date())
				.withExpiresAt(new Date(Calendar.getInstance().getTimeInMillis() + (msMinute * 15)))
				.withClaim("id", id)
				.sign(algorithm);
		return jwt;
	}
	
	public static Boolean verifyToken(String token) {
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
	
	public static Boolean tokenIsValid(String token) {
		if(token == null || !JWTHandler.verifyToken(token)) {
			return false;
		}
		return true;
	}
	
}
