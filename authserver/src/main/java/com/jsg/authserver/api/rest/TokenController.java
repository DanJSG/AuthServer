package com.jsg.authserver.api.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.authserver.datatypes.AppAuthRecord;
import com.jsg.authserver.datatypes.AuthCode;
import com.jsg.authserver.datatypes.CodeChallenge;
import com.jsg.authserver.datatypes.TokenPair;
import com.jsg.authserver.repositories.AppAuthRecordRepository;
import com.jsg.authserver.repositories.AuthCodeRepository;
import com.jsg.authserver.repositories.CodeChallengeRepository;
import com.jsg.authserver.repositories.TokenPairRepository;
import com.jsg.authserver.tokenhandlers.JWTHandler;

@RestController
public final class TokenController extends ApiController {

	@Autowired
	public TokenController(@Value("${token.expiry.access}") int accessTokenExpiryTime,
							@Value("${token.expiry.refresh}") int refreshTokenExpiryTime,
							@Value("${token.secret.refresh}") String refreshTokenSecret,
							@Value("${sql.username}") String sqlUsername,
							@Value("${sql.password}") String sqlPassword,
							@Value("${sql.connectionstring}") String sqlConnectionString) {
		super(accessTokenExpiryTime, refreshTokenExpiryTime, refreshTokenSecret,
				sqlUsername, sqlPassword, sqlConnectionString);
	}
	
	@PostMapping(value = "/token")
	public @ResponseBody ResponseEntity<String> token(HttpServletResponse response,
			@RequestParam(required=false) String code, @RequestParam(required=false) String state,
			@RequestParam(required=false) String redirect_uri, @RequestParam(required=false) String code_verifier,
			@RequestParam(required=false) String refresh_token, @RequestParam String client_id,
			@RequestParam String grant_type, @CookieValue(name = REFRESH_TOKEN_NAME, required = false) String refreshCookie) 
					throws Exception {
		switch(grant_type) {
			case AUTH_CODE_GRANT_TYPE:
				return getRefreshTokenWithAuthCode(code, state, client_id, redirect_uri, code_verifier, response);
			case REFRESH_TOKEN_GRANT_TYPE:
				return getAccessToken(client_id, refresh_token, refreshCookie, response);
			default:
				return UNAUTHORIZED_HTTP_RESPONSE;
		}
	}
	
	private ResponseEntity<String> getAccessToken(String client_id, String refresh_token,
			String refreshCookie, HttpServletResponse response) throws Exception {
		if(refresh_token == null || refreshCookie == null) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		TokenPairRepository tokenRepo = new TokenPairRepository(SQL_CONNECTION_STRING, SQL_USERNAME, SQL_PASSWORD);
		TokenPair tokenPair = new TokenPair(client_id, refreshCookie, refresh_token);
		if(!tokenPair.verifyRefreshTokens(tokenRepo, REFRESH_TOKEN_SECRET)) {
			tokenRepo.closeConnection();
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		tokenRepo.closeConnection();
		AppAuthRecordRepository appRepo = new AppAuthRecordRepository(SQL_CONNECTION_STRING, SQL_USERNAME, SQL_PASSWORD);
		List<AppAuthRecord> appList = appRepo.findWhereEqual("client_id", client_id);
		appRepo.closeConnection();
		if(appList == null || appList.size() < 1) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		AppAuthRecord app = appList.get(0);
		String cookieToken = JWTHandler.createToken(JWTHandler.getIdFromToken(refreshCookie), app.getAccessTokenSecret(), ACCESS_TOKEN_EXPIRY_TIME);
		String headerToken = JWTHandler.createToken(JWTHandler.getIdFromToken(refresh_token), app.getAccessTokenSecret(), ACCESS_TOKEN_EXPIRY_TIME);
		response.addCookie(createCookie(ACCESS_TOKEN_NAME, cookieToken, ACCESS_TOKEN_EXPIRY_TIME));
		Map<String, String> responseBody = new HashMap<>();
		responseBody.put("token", headerToken);
		return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().writeValueAsString(responseBody));
	}
	
	private ResponseEntity<String> getRefreshTokenWithAuthCode(String code, String state, String client_id, String redirect_uri,
			String code_verifier, HttpServletResponse response) throws Exception {
		if(code == null || state == null || client_id == null || redirect_uri == null || code_verifier == null) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		AppAuthRecordRepository appRepo = new AppAuthRecordRepository(SQL_CONNECTION_STRING, SQL_USERNAME, SQL_PASSWORD);
		AppAuthRecord app = new AppAuthRecord(client_id, redirect_uri);
		if(!app.verifyAppAuthRecord(appRepo)) {
			appRepo.closeConnection();
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		appRepo.closeConnection();
		AuthCodeRepository authRepo = new AuthCodeRepository(SQL_CONNECTION_STRING, SQL_USERNAME, SQL_PASSWORD);
		AuthCode authCode = new AuthCode(client_id, code);
		if(!authCode.verifyAuthCode(authRepo)) {
			authRepo.closeConnection();
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		authRepo.closeConnection();
		CodeChallengeRepository challengeRepo = new CodeChallengeRepository(SQL_CONNECTION_STRING, SQL_USERNAME, SQL_PASSWORD);
		CodeChallenge challenge = new CodeChallenge(client_id, state);
		if(!challenge.verifyCodeChallenge(challengeRepo, code_verifier)) {
			challengeRepo.closeConnection();
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		challengeRepo.closeConnection();
		String cookieToken = JWTHandler.createToken(authCode.getUserId(), REFRESH_TOKEN_SECRET, REFRESH_TOKEN_EXPIRY_TIME);
		String headerToken = JWTHandler.createToken(authCode.getUserId(), REFRESH_TOKEN_SECRET, REFRESH_TOKEN_EXPIRY_TIME);
		TokenPair tokenPair = new TokenPair(client_id, cookieToken, headerToken);
		if(!tokenPair.save(SQL_CONNECTION_STRING, SQL_USERNAME, SQL_PASSWORD)) {
			System.out.println("Problem saving tokens");
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		response.addCookie(createCookie(REFRESH_TOKEN_NAME, cookieToken, REFRESH_TOKEN_EXPIRY_TIME));
		Map<String, String> responseBody = new HashMap<>();
		responseBody.put("token", headerToken);
		return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().writeValueAsString(responseBody));
	}
	
}
