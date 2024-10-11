package com.alom.login.service.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.alom.constant.ApiResponseCode;
import com.alom.constant.ApiResponseMessage;
import com.alom.constant.Result;
import com.alom.login.jwt.JwtService;
import com.alom.login.model.Users;
import com.alom.login.repository.UserRepository;
import com.alom.login.service.UsersService;
import com.alom.payload.GenericResponse;

@Service
public class UsersServiceImpl implements UsersService{

	@Autowired
	private UserRepository userRepository;
	
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(15);
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtService jwtService;
	
	@Override
	public GenericResponse register(Users users) {
		users.setUserPassword(encoder.encode(users.getUserPassword()));
		Users foundedUser = userRepository.findByUsername(users.getUsername());
		if(Objects.nonNull(foundedUser))
			return GenericResponse.builder().result(Result.FAILED).responseCode(ApiResponseCode.FAILED).message(ApiResponseMessage.USER_ALREADY_EXIST).build();
		
		Users registered = userRepository.save(users);
		if(registered.getUserId() != null)
			return GenericResponse.builder().result(Result.SUCCESS).responseCode(ApiResponseCode.SUCCESS).message(ApiResponseMessage.USER_REGISTERED_SUCCESSFULLY).build();
		else
			return GenericResponse.builder().result(Result.FAILED).responseCode(ApiResponseCode.FAILED).message(ApiResponseMessage.USER_REGISTRATION_FAILED).build();
	}

	@Override
	public String verify(String username, String userPassword) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, userPassword));
		
		if(authentication.isAuthenticated()) {
			return jwtService.generateToken(username);
		}
		return ApiResponseMessage.FAILED_TO_LOGGED_IN;
	}

}
