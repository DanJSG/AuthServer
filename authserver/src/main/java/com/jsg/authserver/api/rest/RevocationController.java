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
import com.jsg.authserver.libs.sql.MySQLRepository;
import com.jsg.authserver.libs.sql.SQLColumn;
import com.jsg.authserver.libs.sql.SQLRepository;
import com.jsg.authserver.libs.sql.SQLTable;

@RestController
public final class RevocationController extends ApiController {
	
	@Autowired
	public RevocationController(@Value("${ACCESS_TOKEN_EXPIRES}") int accessTokenExpiryTime,
							@Value("${REFRESH_TOKEN_EXPIRES}") int refreshTokenExpiryTime,
							@Value("${REFRESH_TOKEN_SECRET}") String refreshTokenSecret) {
		super(accessTokenExpiryTime, refreshTokenExpiryTime, refreshTokenSecret);
	}
	
	@PostMapping(value = "/revoke")
	public @ResponseBody ResponseEntity<String> revoke(@CookieValue(name=REFRESH_TOKEN_NAME, required=false) String cookieToken,
			@RequestParam String token, @RequestParam String client_id, HttpServletResponse response) throws Exception {
		SQLRepository<TokenPair> tokenRepo = new MySQLRepository<>(SQLTable.TOKENS);
		TokenPair tokenPair = new TokenPair(client_id, cookieToken, token);
		if(!tokenPair.verifyRefreshTokens(REFRESH_TOKEN_SECRET)) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		tokenRepo.updateWhereEquals(SQLColumn.ID, tokenPair.getId(), SQLColumn.EXPIRED, 1);
		response.addCookie(createCookie(REFRESH_TOKEN_NAME, null, 0));
		response.addCookie(createCookie(ACCESS_TOKEN_NAME, null, 0));
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
	
}
