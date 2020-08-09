package com.jsg.authserver.api.rest;

import javax.servlet.http.Cookie;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1")
public abstract class ApiController {

	protected static final String ALPHA_NUM_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	protected static final String AUTH_CODE_GRANT_TYPE = "authorization_code"; 
	protected static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";
	protected static final String CLIENT_CREDENTIALS_GRANT_TYPE = "client_credentials";
	protected static final String REFRESH_TOKEN_NAME = "ref.tok";
	protected static final String ACCESS_TOKEN_NAME = "acc.tok";
	protected static final String CODE_CHALLENGE_METHOD = "S256";
	protected static final ResponseEntity<String> UNAUTHORIZED_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	protected static final ResponseEntity<String> BAD_REQUEST_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	
	protected final int ACCESS_TOKEN_EXPIRY_TIME;
	protected final int REFRESH_TOKEN_EXPIRY_TIME;
	protected final String REFRESH_TOKEN_SECRET;
	
	protected ApiController(int accessTokenExpiryTime, int refreshTokenExpiryTime,
							String refreshTokenSecret) {
		this.ACCESS_TOKEN_EXPIRY_TIME = accessTokenExpiryTime;
		this.REFRESH_TOKEN_EXPIRY_TIME = refreshTokenExpiryTime;
		this.REFRESH_TOKEN_SECRET = refreshTokenSecret;
	}
	
	protected ApiController() {
		this.ACCESS_TOKEN_EXPIRY_TIME = -1;
		this.REFRESH_TOKEN_EXPIRY_TIME = -1;
		this.REFRESH_TOKEN_SECRET = null;
	}
	
	protected Cookie createCookie(String name, String value, int expires) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(expires);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}
	
}
