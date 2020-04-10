package com.jsg.authserver.api.rest;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.bson.internal.Base64;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jsg.authserver.datatypes.TokenPair;
import com.jsg.authserver.datatypes.User;
import com.jsg.authserver.repositories.TokenRepository;
import com.jsg.authserver.repositories.UserRepository;
import com.jsg.authserver.tokenhandlers.AuthHeaderHandler;
import com.jsg.authserver.tokenhandlers.JWTHandler;

@RestController
@CrossOrigin(origins = "http://local.courier.net:3000")
@RequestMapping("/api/auth")
public final class AuthController {
	
	private static final String refreshTokenCookieName = "ref.tok";
	private static final String accessTokenCookieName = "acc.tok";
	
	private final int accessExpiryTime;
	private final int refreshExpiryTime;
	private final String refreshSecret;
	private final String accessSecret;
	private final String sqlUsername;
	private final String sqlPassword;
	private final String sqlConnectionString;
	
	@Autowired
	public AuthController(@Value("${accessTokenExpiryTime}") int accessTokenExpiryTime,
							@Value("${refreshTokenExpiryTime}") int refreshTokenExpiryTime,
							@Value("${refreshTokenSecret}") String refreshTokenSecret,
							@Value("${accessTokenSecret}") String accessTokenSecret,
							@Value("${sqlUsername}") String sqlUsername,
							@Value("${sqlPassword}") String sqlPassword,
							@Value("${sqlConnectionString}") String sqlConnectionString) {
		this.accessExpiryTime = accessTokenExpiryTime;
		this.accessSecret = accessTokenSecret;
		this.refreshExpiryTime = refreshTokenExpiryTime;
		this.refreshSecret = refreshTokenSecret;
		this.sqlConnectionString = sqlConnectionString;
		this.sqlUsername = sqlUsername;
		this.sqlPassword = sqlPassword;
	}
	
	@CrossOrigin(origins = "http://local.courier.net:3000/*", allowCredentials="true", exposedHeaders="Authorization")
	@PostMapping(value = "/authorize", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Boolean> authorize(@RequestBody Map<String, String> userLogin, HttpServletResponse response) throws Exception {
		User user = verifyCredentials(userLogin.get("email"), userLogin.get("password"));
		if(user == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		}
		String refreshToken = JWTHandler.createToken(user.getId(), refreshSecret, refreshExpiryTime);
		String xsrfRefreshToken = JWTHandler.createToken(user.getId(), refreshSecret, refreshExpiryTime);
		TokenRepository tokenRepo = new TokenRepository(sqlConnectionString, sqlUsername, sqlPassword);
		if(!tokenRepo.save(new TokenPair(refreshToken, xsrfRefreshToken, false))) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		tokenRepo.closeConnection();
		response.addCookie(createAuthCookie(refreshTokenCookieName, refreshToken, refreshExpiryTime));
		return ResponseEntity.status(HttpStatus.OK).header("Authorization", "Bearer " + xsrfRefreshToken).body(true);
	}
	
	@CrossOrigin(origins = "http://local.courier.net:3000/*", allowCredentials="true", exposedHeaders="Authorization")
	@PostMapping(value = "/refresh")
	public @ResponseBody ResponseEntity<Boolean> refresh(@CookieValue(name = refreshTokenCookieName, required = false) String cookieToken,
			@RequestHeader String authorization, HttpServletResponse response) throws Exception {
		String headerToken = AuthHeaderHandler.getBearerToken(authorization);
		if(!JWTHandler.tokenIsValid(cookieToken, refreshSecret) || !JWTHandler.tokenIsValid(headerToken, refreshSecret)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		TokenRepository tokenRepo = new TokenRepository(sqlConnectionString, sqlUsername, sqlPassword);
		TokenPair tokenPair = verifyRefreshTokens(tokenRepo, cookieToken, headerToken);
		if(tokenPair == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		tokenRepo.closeConnection();
		String accessToken = JWTHandler.createToken(JWTHandler.getIdFromToken(cookieToken), accessSecret, accessExpiryTime);
		String xsrfAccessToken = JWTHandler.createToken(JWTHandler.getIdFromToken(headerToken), accessSecret, accessExpiryTime);
		response.addCookie(createAuthCookie(accessTokenCookieName, accessToken, accessExpiryTime));
		return ResponseEntity.status(HttpStatus.OK).header("Authorization", "Bearer " + xsrfAccessToken).body(true);
	}
	
	@CrossOrigin(origins = "http://local.courier.net:3000/*", allowCredentials="true", exposedHeaders="Authorization")
	@PostMapping(value = "/revoke")
	public @ResponseBody ResponseEntity<Boolean> revoke(@CookieValue(name = refreshTokenCookieName, required = false) String cookieToken,
			@RequestHeader String authorization, HttpServletResponse response) throws Exception {
		String headerToken = AuthHeaderHandler.getBearerToken(authorization);
		if(!JWTHandler.tokenIsValid(cookieToken, refreshSecret) || !JWTHandler.tokenIsValid(headerToken, refreshSecret)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		TokenRepository tokenRepo = new TokenRepository(sqlConnectionString, sqlUsername, sqlPassword);
		TokenPair tokenPair = verifyRefreshTokens(tokenRepo, cookieToken, headerToken);
		if(tokenPair == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		tokenRepo.updateWhereEquals("id", tokenPair.getId(), "expired", 1);
		tokenRepo.closeConnection();
		response.addCookie(createAuthCookie(refreshTokenCookieName, "", -1));
		response.addCookie(createAuthCookie(accessTokenCookieName, "", -1));
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
	private TokenPair verifyRefreshTokens(TokenRepository tokenRepo, String cookieToken, String headerToken) {
		List<TokenPair> results = tokenRepo.findWhereEqual("tokenA", cookieToken, 1);
		if(results == null || results.size() < 1) {
			return null;
		}
		TokenPair tokenPair = results.get(0);
		if(tokenPair.isExpired()) {
			return null;
		}
		if(!headerToken.contentEquals(tokenPair.getHeaderToken())) {
			return null;
		}
		return tokenPair;
	}
	
	private User verifyCredentials(String email, String password) throws Exception {
		UserRepository userRepo = new UserRepository(sqlConnectionString, sqlUsername, sqlPassword);
		List<User> results = userRepo.findWhereEqual("email", email, 1);
		userRepo.closeConnection();
		if(results == null || results.size() < 1) {
			return null;
		}
		User user = results.get(0);
		if(!BCrypt.checkpw(new String(Base64.decode(password)), user.getPassword())) {
			return null;
		}
		user.clearPassword();
		return user;
	}
	
	private Cookie createAuthCookie(String name, String value, int expires) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(expires);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}
	
}
