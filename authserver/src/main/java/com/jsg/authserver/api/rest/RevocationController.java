package com.jsg.authserver.api.rest;

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

import com.jsg.authserver.datatypes.TokenPair;
import com.jsg.authserver.repositories.TokenPairRepository;

@RestController
public final class RevocationController extends ApiController {
	
	@Autowired
	public RevocationController(@Value("${token.expiry.access}") int accessTokenExpiryTime,
							@Value("${token.expiry.refresh}") int refreshTokenExpiryTime,
							@Value("${token.secret.refresh}") String refreshTokenSecret,
							@Value("${token.secret.access}") String accessTokenSecret,
							@Value("${sql.username}") String sqlUsername,
							@Value("${sql.password}") String sqlPassword,
							@Value("${sql.connectionstring}") String sqlConnectionString) {
		super(accessTokenExpiryTime, refreshTokenExpiryTime, refreshTokenSecret, accessTokenSecret,
				sqlUsername, sqlPassword, sqlConnectionString);
	}
	
	@PostMapping(value = "/revoke")
	public @ResponseBody ResponseEntity<String> revoke(@CookieValue(name=REFRESH_TOKEN_NAME, required=false) String cookieToken,
			@RequestParam String token, @RequestParam String client_id, HttpServletResponse response) throws Exception {
		TokenPairRepository tokenRepo = new TokenPairRepository(SQL_CONNECTION_STRING, SQL_USERNAME, SQL_PASSWORD);
		TokenPair tokenPair = new TokenPair(client_id, cookieToken, token);
		if(!tokenPair.verifyRefreshTokens(tokenRepo, REFRESH_TOKEN_SECRET)) {
			tokenRepo.closeConnection();
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		tokenRepo.updateWhereEquals("id", tokenPair.getId(), "expired", 1);
		tokenRepo.closeConnection();
		response.addCookie(createCookie(REFRESH_TOKEN_NAME, null, 0));
		response.addCookie(createCookie(ACCESS_TOKEN_NAME, null, 0));
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
	
}
