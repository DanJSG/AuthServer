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
import com.jsg.authserver.auth.JWTHandler;
import com.jsg.authserver.datatypes.App;
import com.jsg.authserver.datatypes.AppBuilder;
import com.jsg.authserver.datatypes.AuthCode;
import com.jsg.authserver.datatypes.Challenge;
import com.jsg.authserver.datatypes.TokenPair;
import com.jsg.authserver.datatypes.UserInfo;
import com.jsg.authserver.datatypes.UserInfoBuilder;
import com.jsg.authserver.libs.sql.MySQLRepository;
import com.jsg.authserver.libs.sql.SQLColumn;
import com.jsg.authserver.libs.sql.SQLRepository;
import com.jsg.authserver.libs.sql.SQLTable;

@RestController
public final class TokenController extends ApiController {

	@Autowired
	public TokenController(@Value("${ACCESS_TOKEN_EXPIRES}") int accessTokenExpiryTime,
							@Value("${REFRESH_TOKEN_EXPIRES}") int refreshTokenExpiryTime,
							@Value("${REFRESH_TOKEN_SECRET}") String refreshTokenSecret) {
		super(accessTokenExpiryTime, refreshTokenExpiryTime, refreshTokenSecret);
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
		SQLRepository<App> repo = new MySQLRepository<>(SQLTable.APPS);
		List<App> appList = repo.findWhereEqual(SQLColumn.CLIENT_ID, client_id, new AppBuilder());
		if(appList == null || appList.size() < 1) {
			return BAD_REQUEST_HTTP_RESPONSE;
		}
		App app = appList.get(0);
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
		TokenPair tokenPair = new TokenPair(client_id, refreshCookie, refresh_token);
		if(!tokenPair.verifyRefreshTokens(REFRESH_TOKEN_SECRET)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		SQLRepository<App> appRepo = new MySQLRepository<>(SQLTable.APPS);
		List<App> appList = appRepo.findWhereEqual(SQLColumn.CLIENT_ID, client_id, new AppBuilder());
		if(appList == null || appList.size() < 1) {
			return BAD_REQUEST_HTTP_RESPONSE;
		}
		App app = appList.get(0);
		return getAccessToken(JWTHandler.getIdFromToken(refresh_token), app.getAccessTokenSecret(), response);
	}
	
	private ResponseEntity<String> getAccessToken(long id, String secret, HttpServletResponse response) throws Exception {
		if(id < 0 || secret == null || response == null) {
			return BAD_REQUEST_HTTP_RESPONSE;
		}
		SQLRepository<UserInfo> infoRepo = new MySQLRepository<>(SQLTable.INFO);
		List<UserInfo> info = infoRepo.findWhereEqual(SQLColumn.ID, id, new UserInfoBuilder());
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
		App app = new App(client_id, redirect_uri);
		if(!app.verifyAppAuthRecord()) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		AuthCode authCode = new AuthCode(client_id, code);
		if(!authCode.verifyAuthCode()) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		Challenge challenge = new Challenge(client_id, state);
		if(!challenge.verifyCodeChallenge(code_verifier)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		String cookieToken = JWTHandler.createToken(authCode.getUserId(), REFRESH_TOKEN_SECRET, REFRESH_TOKEN_EXPIRY_TIME);
		String headerToken = JWTHandler.createToken(authCode.getUserId(), REFRESH_TOKEN_SECRET, REFRESH_TOKEN_EXPIRY_TIME);
		TokenPair tokenPair = new TokenPair(client_id, cookieToken, headerToken);
		SQLRepository<TokenPair> tokenRepo = new MySQLRepository<>(SQLTable.TOKENS);
		if(!tokenRepo.save(tokenPair)) {
			System.out.println("Problem saving tokens");
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		response.addCookie(createCookie(REFRESH_TOKEN_NAME, cookieToken, REFRESH_TOKEN_EXPIRY_TIME));
		Map<String, String> responseBody = new HashMap<>();
		responseBody.put("token", headerToken);
		return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().writeValueAsString(responseBody));
	}
	
}
