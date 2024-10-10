package com.alom.login.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alom.login.model.Users;
import com.alom.login.service.UsersService;

@RestController
@RequestMapping("/api/auth")
public class LoginApi {

	@Autowired
	private UsersService usersService;
	
	@PostMapping("/register")
	public ResponseEntity<Users> register(@RequestBody Users users) {
		Users registered = usersService.register(users);
		return ResponseEntity.ok(registered);
	}
	
	@PostMapping("/login")
	public String login(@RequestBody Users user) {
		
		return usersService.verify(user);
	}
	
}
