package com.jsg.authserver.api.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.authserver.datatypes.AppAuthRecord;
import com.jsg.authserver.datatypes.UserInfo;
import com.jsg.authserver.repositories.AppAuthRecordRepository;
import com.jsg.authserver.repositories.UserInfoRepository;
import com.jsg.authserver.tokenhandlers.AuthHeaderHandler;
import com.jsg.authserver.tokenhandlers.JWTHandler;

@RestController
public class UserInfoController extends ApiController {

	protected UserInfoController(@Value("${sql.username}") String sqlUsername,
			@Value("${sql.password}") String sqlPassword,
			@Value("${sql.connectionstring}") String sqlConnectionString) {
		super(sqlUsername, sqlPassword, sqlConnectionString);
	}
	
	@GetMapping("/userInfo")
	public ResponseEntity<String> getUserInfo(@RequestParam String client_id, @RequestParam long id,
			@CookieValue(name = ACCESS_TOKEN_NAME, required = false) String jwt,
			@RequestHeader String authorization) throws Exception {
		AppAuthRecordRepository appRepo = new AppAuthRecordRepository(SQL_CONNECTION_STRING, SQL_USERNAME, SQL_PASSWORD);
		List<AppAuthRecord> appList = appRepo.findWhereEqual("client_id", client_id, 1);
		appRepo.closeConnection();
		if(appList == null || appList.size() < 1) {
			return BAD_REQUEST_HTTP_RESPONSE;
		}
		AppAuthRecord app = appList.get(0);
		String headerJwt = AuthHeaderHandler.getBearerToken(authorization);
		if(!JWTHandler.tokenIsValid(jwt, app.getAccessTokenSecret()) || !JWTHandler.tokenIsValid(headerJwt, app.getAccessTokenSecret())) {
			return UNAUTHORIZED_HTTP_RESPONSE;
		}
		UserInfoRepository infoRepo = new UserInfoRepository(SQL_CONNECTION_STRING, SQL_USERNAME, SQL_PASSWORD);
		List<UserInfo> infoList = infoRepo.findWhereEqual("id", id, 1);
		if(infoList == null || infoList.size() < 1) {
			return BAD_REQUEST_HTTP_RESPONSE;
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().writeValueAsString(infoList.get(0)));
	}

}
