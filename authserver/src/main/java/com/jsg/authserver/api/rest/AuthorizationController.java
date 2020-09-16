package com.jsg.authserver.api.rest;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.authserver.datatypes.App;
import com.jsg.authserver.datatypes.AuthCode;
import com.jsg.authserver.datatypes.Challenge;
import com.jsg.authserver.datatypes.LoginCredentials;
import com.jsg.authserver.datatypes.User;
import com.jsg.authserver.helpers.SecureRandomString;
import com.jsg.authserver.libs.sql.MySQLRepository;
import com.jsg.authserver.libs.sql.SQLRepository;
import com.jsg.authserver.libs.sql.SQLTable;

@RestController
public final class AuthorizationController extends ApiController {
	
	@Autowired
	public AuthorizationController(@Value("${ACCESS_TOKEN_EXPIRES}") int accessTokenExpiryTime,
							@Value("${REFRESH_TOKEN_EXPIRES}") int refreshTokenExpiryTime,
							@Value("${REFRESH_TOKEN_SECRET}") String refreshTokenSecret,
							@Value("${CLIENT_ID}") String clientId, 
							@Value("${REDIRECT_URI}") String redirectUri, 
							@Value("${ACCESS_TOKEN_SECRET}") String accessTokenSecret) {
		super(accessTokenExpiryTime, refreshTokenExpiryTime, refreshTokenSecret, clientId, redirectUri, accessTokenSecret);
	}
	
	@PostMapping(value = "/authorize", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> authorize(@RequestBody Map<String, String> body,
			@RequestParam String code_challenge, @RequestParam String state, 
			@RequestParam String response_type, @RequestParam(required = false) String redirect_uri,
			@RequestParam(required = false) String client_id, @RequestParam String code_challenge_method,
			HttpServletResponse response) throws Exception {
		if(client_id == null || redirect_uri == null) {
			client_id = CLIENT_ID;
			redirect_uri = REDIRECT_URI;
		}
		if(!code_challenge_method.contentEquals(CODE_CHALLENGE_METHOD)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		LoginCredentials credentials = new LoginCredentials(body);
		User user = new User(credentials.getEmail(), credentials.getPassword());
		if(!user.verifyCredentials()) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		App app = new App(client_id, redirect_uri);
		if(!app.verifyAppAuthRecord()) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		Challenge challenge = new Challenge(client_id, code_challenge, state);
		SQLRepository<Challenge> challengeRepo = new MySQLRepository<>(SQLTable.CHALLENGES);
		if(!challengeRepo.save(challenge)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		AuthCode authCode = new AuthCode(client_id, user.getId(), SecureRandomString.getAlphaNumeric(24));
		SQLRepository<AuthCode> authRepo = new MySQLRepository<>(SQLTable.CODES);
		if(!authRepo.save(authCode)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().createObjectNode().put("code", authCode.getCode()).toString());
	}
	
}
