package com.jsg.authserver.api.rest;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.jsg.authserver.datatypes.AppAuthRecord;
import com.jsg.authserver.datatypes.AuthCode;
import com.jsg.authserver.datatypes.CodeChallenge;
import com.jsg.authserver.datatypes.LoginCredentials;
import com.jsg.authserver.datatypes.TokenPair;
import com.jsg.authserver.datatypes.User;
import com.jsg.authserver.repositories.AppAuthRecordRepository;
import com.jsg.authserver.repositories.AuthCodeRepository;
import com.jsg.authserver.repositories.CodeChallengeRepository;
import com.jsg.authserver.repositories.TokenPairRepository;
import com.jsg.authserver.repositories.UserRepository;
import com.jsg.authserver.tokenhandlers.JWTHandler;

@RestController
@CrossOrigin(origins = "http://local.courier.net:3000")
@RequestMapping("/api/auth")
public final class AuthController {
	
	private static final String ALPHA_NUM_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	private static final String AUTH_CODE_GRANT_TYPE = "authorization_code"; 
	private static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";
	
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
	public @ResponseBody ResponseEntity<String> authorize(@RequestBody Map<String, String> body,
			@RequestParam String code_challenge, @RequestParam String state, 
			@RequestParam String response_type, @RequestParam String redirect_uri,
			@RequestParam String client_id, HttpServletResponse response) throws Exception {
		LoginCredentials credentials = new LoginCredentials(body);
		User user = verifyCredentials(credentials.getEmail(), credentials.getPassword());
		ResponseEntity<String> unauthorizedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); 
		if(user == null) {
			return unauthorizedResponse;
		}
		if(!verifyAppAuthRecord(client_id, redirect_uri)) {
			return unauthorizedResponse;
		}
		if(!saveCodeChallenge(client_id, code_challenge, state)) {
			return unauthorizedResponse;
		}
		AuthCode authCode = new AuthCode(client_id, user.getId(), generateSecureRandomString(24));
		if(!saveAuthCode(client_id, authCode)) {
			return unauthorizedResponse;
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().createObjectNode().put("code", authCode.getCode()).toString());
	}
	
	@CrossOrigin(origins = "http://local.courier.net:3000/*", allowCredentials="true", exposedHeaders="Authorization")
	@PostMapping(value = "/token")
	public @ResponseBody ResponseEntity<String> token(HttpServletResponse response,
			@RequestParam(required=false) String code, @RequestParam(required=false) String state,
			@RequestParam(required=false) String redirect_uri, @RequestParam(required=false) String code_verifier,
			@RequestParam(required=false) String refresh_token, @RequestParam(required=false) String client_id,
			@RequestParam String grant_type, @CookieValue(name = refreshTokenCookieName, required = false) String refreshCookie) throws Exception {
		ResponseEntity<String> unauthorizedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); 
		switch(grant_type) {
			case AUTH_CODE_GRANT_TYPE:
				return getRefreshTokenWithAuthCode(code, state, client_id, redirect_uri, code_verifier, response);
			case REFRESH_TOKEN_GRANT_TYPE:
				return getAccessToken(refresh_token, refreshCookie, response);
			default:
				return unauthorizedResponse;
		}
	}
	
	private ResponseEntity<String> getAccessToken(String refresh_token, String refreshCookie, HttpServletResponse response) throws Exception {
		ResponseEntity<String> unauthorizedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		if(refresh_token == null || refreshCookie == null) {
			return unauthorizedResponse;
		}
		if(!JWTHandler.tokenIsValid(refreshCookie, refreshSecret) || !JWTHandler.tokenIsValid(refresh_token, refreshSecret)) {
			return unauthorizedResponse;
		}
		TokenPairRepository tokenRepo = new TokenPairRepository(sqlConnectionString, sqlUsername, sqlPassword);
		TokenPair tokenPair = verifyRefreshTokens(tokenRepo, refreshCookie, refresh_token);
		tokenRepo.closeConnection();
		if(tokenPair == null) {
			return unauthorizedResponse;
		}
		String cookieToken = JWTHandler.createToken(JWTHandler.getIdFromToken(refreshCookie), accessSecret, accessExpiryTime);
		String headerToken = JWTHandler.createToken(JWTHandler.getIdFromToken(refresh_token), accessSecret, accessExpiryTime);
		response.addCookie(createAuthCookie(accessTokenCookieName, cookieToken, accessExpiryTime));
		Map<String, String> responseBody = new HashMap<>();
		responseBody.put("token", headerToken);
		return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().writeValueAsString(responseBody));
	}
	
	private ResponseEntity<String> getRefreshTokenWithAuthCode(String code, String state, String client_id, String redirect_uri,
			String code_verifier, HttpServletResponse response) throws Exception {
		ResponseEntity<String> unauthorizedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		if(code == null || state == null || client_id == null || redirect_uri == null || code_verifier == null) {
			return unauthorizedResponse;
		}
		if(!verifyAppAuthRecord(client_id, redirect_uri)) {
			System.out.println("Problem finding app registration");
			return unauthorizedResponse;
		}
		AuthCode authCode = this.verifyAuthCode(client_id, code);
		if(authCode == null) {
			System.out.println("Problem with Auth Code");
			return unauthorizedResponse;
		}
		if(!verifyCodeChallenge(client_id, code_verifier, state)) {
			System.out.println("Problem with code challenge");
			return unauthorizedResponse;
		}
		String cookieToken = JWTHandler.createToken(authCode.getUserId(), refreshSecret, refreshExpiryTime);
		String headerToken = JWTHandler.createToken(authCode.getUserId(), refreshSecret, refreshExpiryTime);
		TokenPairRepository tokenRepo = new TokenPairRepository(sqlConnectionString, sqlUsername, sqlPassword);
		Boolean areTokensSaved = tokenRepo.save(new TokenPair(cookieToken, headerToken, false));
		tokenRepo.closeConnection();
		if(!areTokensSaved) {
			System.out.println("Problem saving tokens");
			return unauthorizedResponse;
		}
		response.addCookie(createAuthCookie(refreshTokenCookieName, cookieToken, refreshExpiryTime));
		Map<String, String> responseBody = new HashMap<>();
		responseBody.put("token", headerToken);
		return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().writeValueAsString(responseBody));
	}
	
//	@CrossOrigin(origins = "http://local.courier.net:3000/*", allowCredentials="true", exposedHeaders="Authorization")
//	@PostMapping(value = "/refresh")
//	public @ResponseBody ResponseEntity<Boolean> refresh(@CookieValue(name = refreshTokenCookieName, required = false) String cookieToken,
//			@RequestHeader String authorization, HttpServletResponse response) throws Exception {
//		String headerToken = AuthHeaderHandler.getBearerToken(authorization);
//		if(!JWTHandler.tokenIsValid(cookieToken, refreshSecret) || !JWTHandler.tokenIsValid(headerToken, refreshSecret)) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
//		}
//		TokenPairRepository tokenRepo = new TokenPairRepository(sqlConnectionString, sqlUsername, sqlPassword);
//		TokenPair tokenPair = verifyRefreshTokens(tokenRepo, cookieToken, headerToken);
//		tokenRepo.closeConnection();
//		if(tokenPair == null) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
//		}
//		String accessToken = JWTHandler.createToken(JWTHandler.getIdFromToken(cookieToken), accessSecret, accessExpiryTime);
//		String xsrfAccessToken = JWTHandler.createToken(JWTHandler.getIdFromToken(headerToken), accessSecret, accessExpiryTime);
//		response.addCookie(createAuthCookie(accessTokenCookieName, accessToken, accessExpiryTime));
//		return ResponseEntity.status(HttpStatus.OK).header("Authorization", "Bearer " + xsrfAccessToken).body(true);
//	}
//	
//	@CrossOrigin(origins = "http://local.courier.net:3000/*", allowCredentials="true", exposedHeaders="Authorization")
//	@PostMapping(value = "/revoke")
//	public @ResponseBody ResponseEntity<Boolean> revoke(@CookieValue(name = refreshTokenCookieName, required = false) String cookieToken,
//			@RequestHeader String authorization, HttpServletResponse response) throws Exception {
//		String headerToken = AuthHeaderHandler.getBearerToken(authorization);
//		if(!JWTHandler.tokenIsValid(cookieToken, refreshSecret) || !JWTHandler.tokenIsValid(headerToken, refreshSecret)) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
//		}
//		TokenPairRepository tokenRepo = new TokenPairRepository(sqlConnectionString, sqlUsername, sqlPassword);
//		TokenPair tokenPair = verifyRefreshTokens(tokenRepo, cookieToken, headerToken);
//		if(tokenPair == null) {
//			tokenRepo.closeConnection();
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
//		}
//		tokenRepo.updateWhereEquals("id", tokenPair.getId(), "expired", 1);
//		tokenRepo.closeConnection();
//		response.addCookie(createAuthCookie(refreshTokenCookieName, "", -1));
//		response.addCookie(createAuthCookie(accessTokenCookieName, "", -1));
//		return ResponseEntity.status(HttpStatus.OK).body(true);
//	}
	
	@CrossOrigin(origins = "http://local.courier.net:3000/*", allowCredentials="true")
	@PostMapping(value = "/revoke")
	public @ResponseBody ResponseEntity<String> revoke(@CookieValue(name=refreshTokenCookieName, required=false) String cookieToken,
			@RequestParam String token, @RequestParam String client_id, HttpServletResponse response) throws Exception {
		if(!JWTHandler.tokenIsValid(cookieToken, refreshSecret) || !JWTHandler.tokenIsValid(token, refreshSecret)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		TokenPairRepository tokenRepo = new TokenPairRepository(sqlConnectionString, sqlUsername, sqlPassword);
		TokenPair tokenPair = verifyRefreshTokens(tokenRepo, cookieToken, token);
		if(tokenPair == null) {
			tokenRepo.closeConnection();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		tokenRepo.updateWhereEquals("id", tokenPair.getId(), "expired", 1);
		tokenRepo.closeConnection();
		response.addCookie(createAuthCookie(refreshTokenCookieName, null, 0));
		response.addCookie(createAuthCookie(accessTokenCookieName, null, 0));
		return ResponseEntity.status(HttpStatus.OK).body(null);
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
		List<TokenPair> results = tokenRepo.findWhereEqual("cookieToken", cookieToken, 1);
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
	
	private Boolean verifyAppAuthRecord(String client_id, String redirect_uri) throws Exception {
		AppAuthRecordRepository appRepo = new AppAuthRecordRepository(sqlConnectionString, sqlUsername, sqlPassword);
		List<AppAuthRecord> appList = appRepo.findWhereEqual("client_id", client_id, 1);
		appRepo.closeConnection();
		if(appList == null || appList.size() < 1) {
			return false;
		}
		AppAuthRecord app = appList.get(0);
		if(!app.getRedirectUri().contentEquals(redirect_uri)) {
			return false;
		}
		return true;
	}
	
	private Boolean saveCodeChallenge(String client_id, String code_challenge, String state) throws Exception {
		CodeChallenge challenge = new CodeChallenge(client_id, code_challenge, state);
		CodeChallengeRepository challengeRepo = new CodeChallengeRepository(sqlConnectionString, sqlUsername, sqlPassword);
		Boolean isSaved = challengeRepo.save(challenge);
		challengeRepo.closeConnection();
		return isSaved;
	}
	
	private Boolean verifyCodeChallenge(String client_id, String code_verifier, String state) throws Exception {
		CodeChallengeRepository challengeRepo = new CodeChallengeRepository(sqlConnectionString, sqlUsername, sqlPassword);
		List<CodeChallenge> codeChallenges = challengeRepo.findWhereEqual("state", state);
		challengeRepo.closeConnection();
		if(codeChallenges == null || codeChallenges.size() < 1) {
			System.out.println("No code challenges found with state: " + state);
			return false;
		}
		CodeChallenge codeChallenge = codeChallenges.get(0);
		if(codeChallenge.getExpiryDateTime().before(new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())) || 
				!codeChallenge.getClientId().contentEquals(client_id)) {
			System.out.println("Code challenge is expired or doesn't match client id");
			return false;
		}
		String codeHash = Hashing.sha256().hashString(code_verifier, Charsets.UTF_8).toString();
		if(!codeChallenge.getCodeChallenge().contentEquals(codeHash)) {
			return false;
		}
		return true;
	}
	
	private Boolean saveAuthCode(String client_id, AuthCode authCode) throws Exception {
		AuthCodeRepository authRepo = new AuthCodeRepository(sqlConnectionString, sqlUsername, sqlPassword);
		Boolean isSaved = authRepo.save(authCode);
		authRepo.closeConnection();
		return isSaved;
	}
	
	private AuthCode verifyAuthCode(String client_id, String code) throws Exception {
		AuthCodeRepository authRepo = new AuthCodeRepository(sqlConnectionString, sqlUsername, sqlPassword);
		List<AuthCode> authCodes = authRepo.findWhereEqual("code", code, 1);
		authRepo.closeConnection();
		if(authCodes == null || authCodes.size() < 1) {
			return null;
		}
		AuthCode authCode = authCodes.get(0);
		if(authCode.getExpiryDateTime().before(new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())) ||
				!authCode.getCode().contentEquals(code)) {
			return null;
		}
		return authCode;
	}
	
	private Cookie createAuthCookie(String name, String value, int expires) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(expires);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}
	
}
