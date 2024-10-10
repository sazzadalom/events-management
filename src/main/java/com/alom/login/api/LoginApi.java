package com.alom.login.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alom.login.model.Users;
import com.alom.login.service.UsersService;
import com.alom.payload.GenericResponse;

@RestController
@RequestMapping("/api/auth")
public class LoginApi {

	@Autowired
	private UsersService usersService;
	
	@PostMapping("/register")
	public ResponseEntity<GenericResponse> register(@RequestBody Users users) {
		GenericResponse response = usersService.register(users);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/generate-token")
	public String login(@RequestHeader("username") String username, @RequestHeader("userPassword") String userPassword) {
		
		return usersService.verify(username,userPassword);
	}
	
}
