package com.alom.login.service;

import com.alom.login.model.Users;

public interface UsersService {
	public Users register(Users users);

	public String verify(Users user);

}
