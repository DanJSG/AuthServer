package com.jsg.authserver.api.rest;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.bson.internal.Base64;
import org.mindrot.jbcrypt.BCrypt;
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

import com.jsg.authserver.datatypes.User;
import com.jsg.authserver.repositories.UserRepository;
import com.jsg.authserver.tokenhandlers.AccessTokenHandler;
import com.jsg.authserver.tokenhandlers.AuthHeaderHandler;
import com.jsg.authserver.tokenhandlers.JWTHandler;
import com.jsg.authserver.tokenhandlers.RefreshTokenHandler;

@RestController
@CrossOrigin(origins = "http://local.courier.net:3000")
@RequestMapping("/api/auth")
public class AuthController {
	
	private static final String refreshTokenCookieName = "ref.tok";
	private static final String accessTokenCookieName = "acc.tok";
	
	@CrossOrigin(origins = "http://local.courier.net:3000/*", allowCredentials="true", exposedHeaders="Authorization")
	@PostMapping(value = "/authorize", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Boolean> authorize(@RequestBody Map<String, String> userLogin, HttpServletResponse response) throws Exception {
		UserRepository userRepo = new UserRepository();
		List<User> results = userRepo.findWhereEqual("email", userLogin.get("email"), 1);
		if(results == null || results.size() < 1) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		}
		User user = results.get(0);
		if(!BCrypt.checkpw(new String(Base64.decode(userLogin.get("password"))), user.getPassword())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		}
		user.clearPassword();
		String refreshToken = RefreshTokenHandler.createToken(user.getId());
		String xsrfRefreshToken = RefreshTokenHandler.createToken(user.getId());
		response.addCookie(createAuthCookie(refreshTokenCookieName, refreshToken));
		return ResponseEntity.status(HttpStatus.OK).header("Authorization", "Bearer " + xsrfRefreshToken).body(true);
	}
	
	@CrossOrigin(origins = "http://local.courier.net:3000/*", allowCredentials="true", exposedHeaders="Authorization")
	@PostMapping(value = "/refresh")
	public @ResponseBody ResponseEntity<Boolean> refresh(@CookieValue(name = refreshTokenCookieName, required = false) String cookieToken,
			@RequestHeader String authorization, HttpServletResponse response) {
		String headerToken = AuthHeaderHandler.getBearerToken(authorization);
		if(!RefreshTokenHandler.tokenIsValid(cookieToken) || !RefreshTokenHandler.tokenIsValid(headerToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		// TODO lookup refresh token in database and verify
		String accessToken = AccessTokenHandler.createToken(JWTHandler.getIdFromToken(cookieToken));
		String xsrfAccessToken = AccessTokenHandler.createToken(JWTHandler.getIdFromToken(headerToken));
		response.addCookie(createAuthCookie(accessTokenCookieName, accessToken));
		return ResponseEntity.status(HttpStatus.OK).header("Authorization", "Bearer " + xsrfAccessToken).body(true);
	}
	
	@CrossOrigin(origins = "http://local.courier.net:3000/*", allowCredentials="true", exposedHeaders="Authorization")
	@PostMapping(value = "/revoke")
	public @ResponseBody ResponseEntity<Boolean> revoke(@CookieValue(name = refreshTokenCookieName, required = false) String cookieToken,
			@RequestHeader String authorization, HttpServletResponse response) {
		String headerToken = AuthHeaderHandler.getBearerToken(authorization);
		if(!RefreshTokenHandler.tokenIsValid(cookieToken) || !RefreshTokenHandler.tokenIsValid(headerToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		// TODO update refresh token validity in database
		response.addCookie(deleteAuthCookie());
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
	private Cookie createAuthCookie(String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(900);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}
	
	private Cookie deleteAuthCookie() {
		Cookie cookie = new Cookie(refreshTokenCookieName, "");
		cookie.setMaxAge(0);
		return cookie;
	}
	
}
