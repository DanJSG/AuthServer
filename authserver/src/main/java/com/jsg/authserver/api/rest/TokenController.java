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
import com.jsg.authserver.datatypes.AppAuthRecordBuilder;
import com.jsg.authserver.datatypes.AuthCode;
import com.jsg.authserver.datatypes.CodeChallenge;
import com.jsg.authserver.datatypes.TokenPair;
import com.jsg.authserver.datatypes.UserInfo;
import com.jsg.authserver.datatypes.UserInfoBuilder;
import com.jsg.authserver.libs.sql.MySQLRepository;
import com.jsg.authserver.libs.sql.SQLRepository;
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
			@RequestParam(required=false) String refresh_token, @RequestParam(required=false) String client_secret,
			@RequestParam String client_id, @RequestParam String grant_type, 
			@CookieValue(name = REFRESH_TOKEN_NAME, required = false) String refreshCookie) 
					throws Exception {
		switch(grant_type) {
			case AUTH_CODE_GRANT_TYPE:
				return getRefreshTokenWithAuthCode(code, state, client_id, redirect_uri, code_verifier, response);
			case REFRESH_TOKEN_GRANT_TYPE:
				return getAccessTokenWithRefreshToken(client_id, refresh_token, refreshCookie, response);
			case CLIENT_CREDENTIALS_GRANT_TYPE:
				return getAccessTokenWithClientCredentials(client_id, client_secret, response);
			default:
				return UNAUTHORIZED_HTTP_RESPONSE;
		}
	}
	
	private ResponseEntity<String> getAccessTokenWithClientCredentials(String client_id, String client_secret,
			HttpServletResponse response) throws Exception {
		SQLRepository<AppAuthRecord> repo = new MySQLRepository<>("auth.apps");
		List<AppAuthRecord> appList = repo.findWhereEqual("client_id", client_id, new AppAuthRecordBuilder());
		if(appList == null || appList.size() < 1) {
			return BAD_REQUEST_HTTP_RESPONSE;
		}
		AppAuthRecord app = appList.get(0);
		if(!app.getClientSecret().contentEquals(client_secret)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		return getAccessToken(app.getAssociatedAccountId(), app.getAccessTokenSecret(), response);
	}
	
	private ResponseEntity<String> getAccessTokenWithRefreshToken(String client_id, String refresh_token,
			String refreshCookie, HttpServletResponse response) throws Exception {
		if(refresh_token == null || refreshCookie == null) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		SQLRepository<TokenPair> tokenRepo = new MySQLRepository<>("auth.tokens");
		TokenPair tokenPair = new TokenPair(client_id, refreshCookie, refresh_token);
		if(!tokenPair.verifyRefreshTokens(tokenRepo, REFRESH_TOKEN_SECRET)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		SQLRepository<AppAuthRecord> appRepo = new MySQLRepository<>("auth.apps");
		List<AppAuthRecord> appList = appRepo.findWhereEqual("client_id", client_id, new AppAuthRecordBuilder());
		if(appList == null || appList.size() < 1) {
			return BAD_REQUEST_HTTP_RESPONSE;
		}
		AppAuthRecord app = appList.get(0);
		return getAccessToken(JWTHandler.getIdFromToken(refresh_token), app.getAccessTokenSecret(), response);
	}
	
	private ResponseEntity<String> getAccessToken(long id, String secret, HttpServletResponse response) throws Exception {
		if(id < 0 || secret == null || response == null) {
			return BAD_REQUEST_HTTP_RESPONSE;
		}
		SQLRepository<UserInfo> infoRepo = new MySQLRepository<>("users.info");
		List<UserInfo> info = infoRepo.findWhereEqual("id", id, new UserInfoBuilder());
		if(info == null || info.size() == 0) {
			return BAD_REQUEST_HTTP_RESPONSE;
		}
		String name = info.get(0).getDisplayName();
		String cookieToken = JWTHandler.createToken(id, name, secret, ACCESS_TOKEN_EXPIRY_TIME);
		String headerToken = JWTHandler.createToken(id, name, secret, ACCESS_TOKEN_EXPIRY_TIME);
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
		SQLRepository<AppAuthRecord> appRepo = new MySQLRepository<>("auth.apps");
		AppAuthRecord app = new AppAuthRecord(client_id, redirect_uri);
		if(!app.verifyAppAuthRecord(appRepo)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		SQLRepository<AuthCode> authRepo = new MySQLRepository<>("auth.codes");
		AuthCode authCode = new AuthCode(client_id, code);
		if(!authCode.verifyAuthCode(authRepo)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		SQLRepository<CodeChallenge> challengeRepo = new MySQLRepository<>("auth.challenge");
		CodeChallenge challenge = new CodeChallenge(client_id, state);
		if(!challenge.verifyCodeChallenge(challengeRepo, code_verifier)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
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
