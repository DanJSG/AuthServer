package com.jsg.authserver.api.rest;

import java.security.SecureRandom;
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
import com.jsg.authserver.datatypes.AppAuthRecord;
import com.jsg.authserver.datatypes.AuthCode;
import com.jsg.authserver.datatypes.CodeChallenge;
import com.jsg.authserver.datatypes.LoginCredentials;
import com.jsg.authserver.datatypes.User;
import com.jsg.authserver.libs.sql.MySQLRepository;
import com.jsg.authserver.libs.sql.SQLRepository;

@RestController
public final class AuthorizationController extends ApiController {

	@Autowired
	public AuthorizationController(@Value("${token.expiry.access}") int accessTokenExpiryTime,
							@Value("${token.expiry.refresh}") int refreshTokenExpiryTime,
							@Value("${token.secret.refresh}") String refreshTokenSecret) {
		super(accessTokenExpiryTime, refreshTokenExpiryTime, refreshTokenSecret);
	}
	
	@PostMapping(value = "/authorize", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> authorize(@RequestBody Map<String, String> body,
			@RequestParam String code_challenge, @RequestParam String state, 
			@RequestParam String response_type, @RequestParam String redirect_uri,
			@RequestParam String client_id, @RequestParam String code_challenge_method,
			HttpServletResponse response) throws Exception {
		if(!code_challenge_method.contentEquals(CODE_CHALLENGE_METHOD)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		LoginCredentials credentials = new LoginCredentials(body);
		User user = new User(credentials.getEmail(), credentials.getPassword());
		if(!user.verifyCredentials()) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		AppAuthRecord app = new AppAuthRecord(client_id, redirect_uri);
		if(!app.verifyAppAuthRecord()) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		CodeChallenge challenge = new CodeChallenge(client_id, code_challenge, state);
		SQLRepository<CodeChallenge> challengeRepo = new MySQLRepository<>("auth.challenge");
		if(!challengeRepo.save(challenge)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		AuthCode authCode = new AuthCode(client_id, user.getId(), generateSecureRandomString(24));
		SQLRepository<AuthCode> authRepo = new MySQLRepository<>("auth.codes");
		if(!authRepo.save(authCode)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().createObjectNode().put("code", authCode.getCode()).toString());
	}
	
	private String generateSecureRandomString(int length) {
		SecureRandom randomProvider = new SecureRandom();
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=0; i < length; i++) {
			stringBuilder.append(ALPHA_NUM_CHARS.charAt(randomProvider.nextInt(ALPHA_NUM_CHARS.length())));
		}
		return stringBuilder.toString();
	}
	
}
