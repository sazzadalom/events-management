package com.alom.login.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.alom.login.configuration.filter.JwtFilter;
import com.alom.login.service.MyUserDetailService;
/**
 * This class use for configuration purpose.
 * @author sazzad
 * @version 1.0.0
 * @since 09-10-2024
 *
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
	
	@Autowired
	private MyUserDetailService userDetailService;
	
	@Autowired
	private JwtFilter jwtFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
		.csrf(customizer -> customizer.disable())
		.authorizeHttpRequests(customizer -> customizer
				.requestMatchers("/api/auth/**").permitAll()
				.requestMatchers("/api/events/add-edit").hasRole("ADMIN")
				.anyRequest().authenticated())
		.httpBasic(Customizer.withDefaults())
		.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
		.build();
	}
	
	/**
	 * <p>Create authentication provider bean for authentication manager.</p>
	 * @return
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
		provider.setUserDetailsService(userDetailService);
		return provider;
	}
	
	/**
	 * <p>Use for mange authentication object here</p>
	 * @param config
	 * @return
	 * @throws Exception
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	} 
	
	
}
