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

import com.jsg.authserver.datatypes.TokenPair;
import com.jsg.authserver.datatypes.User;
import com.jsg.authserver.repositories.TokenRepository;
import com.jsg.authserver.repositories.UserRepository;
import com.jsg.authserver.tokenhandlers.AuthHeaderHandler;
import com.jsg.authserver.tokenhandlers.JWTHandler;

@RestController
@CrossOrigin(origins = "http://local.courier.net:3000")
@RequestMapping("/api/auth")
public final class AuthController {
	
	private static final String refreshTokenCookieName = "ref.tok";
	private static final String accessTokenCookieName = "acc.tok";
	private static final int accessExpiryTime = 900; // 15 minutes in seconds
	private static final int refreshExpiryTime = 2419200; // 28 days in seconds
	
	private static final String refreshSecret = "L^a:fnQ(ZWe!t;d'qkDF}-M\"<?_2K&;zyw_$#ZwhZr4@<ssP.U"
			+ "!h#FD[kQ@=JWNVZQjM#h>@kWxE(qH3yQ$k8}6P-yA=JN=ZLWA"
			+ "d!M],zu2jm6Fr$LZ_,TarHjeu=Ar!Z53X<j_ueC/5d@psXbS8"
			+ "vF@`uF{ap`hq_d9pBa})-Y3!5`mv55)p7`t~8!V4TS}xCS7rZ"
			+ "&fc~bx:)<`k_D;CnJnmdQK]UjcZ'WGAT<W_w>/f3$}nNnd^G+"
			+ "Lz(^F8*~%#";
	
	private static final String accessSecret = "%Rw_-C8!tDMtAn3^^KZh7N&m6V+3jnQLmh5b55Xv%WZTS?DbQH_"
			+ "Sgt+HLecK8$LXtTxbQZP+Fp@jgeAzeU*D9Yv$*bS66V2bct!X"
			+ "Cq7t=Lm6#d@grZB5eAX$GbJFmbbG#FfJvBqAQ@JBH=NJnfpk5"
			+ "XfVnq^7jDNmtM8^$%2dxPv^?Jb$B6MuP?BQ=4Mm596_%-=fmm"
			+ "AAZd56kMp@^C6^r?TjCkG4V8CeRV@z5=mFNbjaGDJMVUFPPHA"
			+ "wua8*A_BD";;
	
	@CrossOrigin(origins = "http://local.courier.net:3000/*", allowCredentials="true", exposedHeaders="Authorization")
	@PostMapping(value = "/authorize", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<Boolean> authorize(@RequestBody Map<String, String> userLogin, HttpServletResponse response) throws Exception {
		User user = verifyCredentials(userLogin.get("email"), userLogin.get("password"));
		if(user == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		}
		String refreshToken = JWTHandler.createToken(user.getId(), refreshSecret, refreshExpiryTime);
		String xsrfRefreshToken = JWTHandler.createToken(user.getId(), refreshSecret, refreshExpiryTime);
		TokenRepository tokenRepo = new TokenRepository();
		if(!tokenRepo.save(new TokenPair(refreshToken, xsrfRefreshToken))) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
		tokenRepo.closeConnection();
		response.addCookie(createAuthCookie(refreshTokenCookieName, refreshToken, refreshExpiryTime));
		return ResponseEntity.status(HttpStatus.OK).header("Authorization", "Bearer " + xsrfRefreshToken).body(true);
	}
	
	@CrossOrigin(origins = "http://local.courier.net:3000/*", allowCredentials="true", exposedHeaders="Authorization")
	@PostMapping(value = "/refresh")
	public @ResponseBody ResponseEntity<Boolean> refresh(@CookieValue(name = refreshTokenCookieName, required = false) String cookieToken,
			@RequestHeader String authorization, HttpServletResponse response) throws Exception {
		String headerToken = AuthHeaderHandler.getBearerToken(authorization);
		if(!JWTHandler.tokenIsValid(cookieToken, refreshSecret) || !JWTHandler.tokenIsValid(headerToken, refreshSecret)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		TokenRepository tokenRepo = new TokenRepository();
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
		TokenRepository tokenRepo = new TokenRepository();
		TokenPair tokenPair = verifyRefreshTokens(tokenRepo, cookieToken, headerToken);
		if(tokenPair == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
		tokenRepo.updateWhereEquals("id", tokenPair.getId(), "expired", 1);
		tokenRepo.closeConnection();
		response.addCookie(deleteAuthCookie());
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
	
	private TokenPair verifyRefreshTokens(TokenRepository tokenRepo, String cookieToken, String headerToken) {
		List<TokenPair> results = tokenRepo.findWhereEqual("tokenA", cookieToken, 1);
		if(results == null || results.size() < 1) {
			return null;
		}
		TokenPair tokenPair = results.get(0);
		if(!headerToken.contentEquals(tokenPair.getHeaderToken())) {
			return null;
		}
		return tokenPair;
	}
	
	private User verifyCredentials(String email, String password) throws Exception {
		UserRepository userRepo = new UserRepository();
		List<User> results = userRepo.findWhereEqual("email", email, 1);
		userRepo.closeConnection();
		if(results == null || results.size() < 1) {
			return null;
		}
		User user = results.get(0);
		if(!BCrypt.checkpw(new String(Base64.decode(password)), user.getPassword())) {
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
	
	private Cookie deleteAuthCookie() {
		Cookie cookie = new Cookie(refreshTokenCookieName, "");
		cookie.setMaxAge(0);
		return cookie;
	}
	
}
