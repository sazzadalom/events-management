package com.alom.login.model;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class MyUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Users users; 
	
	public MyUserDetails(Users users){
		this.users = users;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority(users.getUsrrole()));
	}

	@Override
	public String getPassword() {
		return users.getUsrpassword();
	}

	@Override
	public String getUsername() {
		return users.getUsername();
	}

}
