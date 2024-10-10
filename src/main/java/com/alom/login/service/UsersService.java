package com.alom.login.service;

import com.alom.login.model.Users;
import com.alom.payload.GenericResponse;

public interface UsersService {
	public GenericResponse register(Users users);

	public String verify(String username, String userPassword);

}
