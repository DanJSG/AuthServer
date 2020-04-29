package com.jsg.authserver.api.rest;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1")
public abstract class ApiController {

	protected static final String ALPHA_NUM_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	protected static final String AUTH_CODE_GRANT_TYPE = "authorization_code"; 
	protected static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";
	protected static final String REFRESH_TOKEN_NAME = "ref.tok";
	protected static final String ACCESS_TOKEN_NAME = "acc.tok";
	protected static final String CODE_CHALLENGE_METHOD = "S256";
	protected static final ResponseEntity<String> UNAUTHORIZED_HTTP_RESPONSE = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	
	protected final int accessExpiryTime;
	protected final int refreshExpiryTime;
	protected final String refreshSecret;
	protected final String accessSecret;
	protected final String sqlUsername;
	protected final String sqlPassword;
	protected final String sqlConnectionString;
	
	@Autowired
	protected ApiController(int accessTokenExpiryTime, int refreshTokenExpiryTime,
							String refreshTokenSecret, String accessTokenSecret,
							String sqlUsername, String sqlPassword,
							String sqlConnectionString) {
		this.accessExpiryTime = accessTokenExpiryTime;
		this.accessSecret = accessTokenSecret;
		this.refreshExpiryTime = refreshTokenExpiryTime;
		this.refreshSecret = refreshTokenSecret;
		this.sqlConnectionString = sqlConnectionString;
		this.sqlUsername = sqlUsername;
		this.sqlPassword = sqlPassword;
	}
	
	protected Cookie createCookie(String name, String value, int expires) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(expires);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}
	
}
