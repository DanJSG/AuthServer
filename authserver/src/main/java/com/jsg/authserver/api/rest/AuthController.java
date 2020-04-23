package com.jsg.authserver.api.rest;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.authserver.datatypes.AppAuthRecord;
import com.jsg.authserver.datatypes.LoginCredentials;
import com.jsg.authserver.datatypes.TokenPair;
import com.jsg.authserver.datatypes.User;
import com.jsg.authserver.repositories.AppAuthRecordRepository;
import com.jsg.authserver.repositories.TokenPairRepository;
import com.jsg.authserver.repositories.UserRepository;
import com.jsg.authserver.tokenhandlers.AuthHeaderHandler;
import com.jsg.authserver.tokenhandlers.JWTHandler;

@RestController
@CrossOrigin(origins = "http://local.courier.net:3000")
@RequestMapping("/api/auth")
public final class AuthController {
	
	private static final String ALPHA_NUM_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
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
	
	@CrossOrigin(origins = "http://local.courier.net:3000/*")
	@PostMapping(value = "/authorize", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> authorize(@RequestBody Map<String, String> body, @RequestParam String code_challenge, 
			@RequestParam String response_type, @RequestParam String redirect_uri,
			@RequestParam String client_id, HttpServletResponse response) throws Exception {
		LoginCredentials credentials = new LoginCredentials(body);
		User user = verifyCredentials(credentials.getEmail(), credentials.getPassword());
		if(user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		String authCode = generateSecureRandomString(24);
		// TODO REFACTOR ALL OF THIS STUFF
		AppAuthRecordRepository appRepo = new AppAuthRecordRepository(sqlConnectionString, sqlUsername, sqlPassword);
		List<AppAuthRecord> appList = appRepo.findWhereEqual("client_id", client_id);
		if(appList.size() < 1) {
			return null;
		}
		AppAuthRecord app = appList.get(0);
		if(!app.getRedirectUri().contentEquals(redirect_uri)) {
			return null;
		}
		// TODO - Add code to store auth code with to client ID and code challenge
		// TODO - Add state column to database, save state with auth code, 16 char secure string
		
		return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().createObjectNode().put("code", authCode).toString());
//		String refreshToken = JWTHandler.createToken(user.getId(), refreshSecret, refreshExpiryTime);
//		String xsrfRefreshToken = JWTHandler.createToken(user.getId(), refreshSecret, refreshExpiryTime);
//		TokenRepository tokenRepo = new TokenRepository(sqlConnectionString, sqlUsername, sqlPassword);
//		if(!tokenRepo.save(new TokenPair(refreshToken, xsrfRefreshToken, false))) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
//		}
//		tokenRepo.closeConnection();
//		response.addCookie(createAuthCookie(refreshTokenCookieName, refreshToken, refreshExpiryTime));
//		return ResponseEntity.status(HttpStatus.OK).header("Authorization", "Bearer " + xsrfRefreshToken).body(true);
	}
	
	@CrossOrigin(origins = "http://local.courier.net:3000/*", allowCredentials="true", exposedHeaders="Authorization")
	@PostMapping(value = "/refresh")
	public @ResponseBody ResponseEntity<Boolean> refresh(@CookieValue(name = refreshTokenCookieName, required = false) String cookieToken,
			@RequestHeader String authorization, HttpServletResponse response) throws Exception {
		String headerToken = AuthHeaderHandler.getBearerToken(authorization);
		if(!JWTHandler.tokenIsValid(cookieToken, refreshSecret) || !JWTHandler.tokenIsValid(headerToken, refreshSecret)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		TokenPairRepository tokenRepo = new TokenPairRepository(sqlConnectionString, sqlUsername, sqlPassword);
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
		TokenPairRepository tokenRepo = new TokenPairRepository(sqlConnectionString, sqlUsername, sqlPassword);
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
	
	private String generateSecureRandomString(int length) {
		SecureRandom randomProvider = new SecureRandom();
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=0; i < length; i++) {
			stringBuilder.append(ALPHA_NUM_CHARS.charAt(randomProvider.nextInt(ALPHA_NUM_CHARS.length())));
		}
		return stringBuilder.toString();
	}
	
	private TokenPair verifyRefreshTokens(TokenPairRepository tokenRepo, String cookieToken, String headerToken) {
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
		if(!BCrypt.checkpw(password, user.getPassword())) {
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
