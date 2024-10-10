package com.alom.login.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.alom.constant.ApiResponseMessage;
import com.alom.login.jwt.JWTService;
import com.alom.login.model.Users;
import com.alom.login.repository.UserRepository;
import com.alom.login.service.UsersService;

@Service
public class UsersServiceImpl implements UsersService{

	@Autowired
	private UserRepository userRepository;
	
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(15);
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JWTService jwtService;
	
	@Override
	public Users register(Users users) {
		users.setUsrpassword(encoder.encode(users.getUsrpassword()));
		return userRepository.save(users);
	}

	@Override
	public String verify(Users user) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getUsrpassword()));
		
		if(authentication.isAuthenticated()) {
			return jwtService.generateToken(user.getUsername());
		}
		return ApiResponseMessage.FAILED_TO_LOGGED_IN;
	}

}
