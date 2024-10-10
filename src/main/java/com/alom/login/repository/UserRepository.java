package com.alom.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alom.login.model.Users;


@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
	public Users findByUsername(String username);

	public Users findByUsernameAndUserRole(String username, String userRole);

}
