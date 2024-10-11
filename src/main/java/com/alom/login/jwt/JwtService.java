package com.alom.login.jwt;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
/**
 * This class is use specify the token beheviour.
 * @author sazzad
 * @version 1.0.0
 * @since 10-10-2024
 *
 */
@Service
public class JwtService {
	

	@Value("${expary.time}")
	private int exparyTime;
	private SecretKey secretKey ;
	
	/**
     * Initialize the secret key after the bean is created.
     */
    @PostConstruct
    public void init() {
        try {
        	secretKey = KeyGenerator.getInstance("HmacSHA256").generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error initializing SecretKey for JWT", e);
        }
    }

	public String generateToken(String username) {
		Map<String, Object> claims = new HashMap<>();

		return Jwts.builder()
				.claims()
				.add(claims)
				.subject(username)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + exparyTime * 60 * 1000))
				.and()
				.signWith(secretKey)
				.compact();
	}

	/**
	 *  Extract userName from JWT token
	 *  
	 * @param token
	 * @return
	 */
	public String extractUsername(String token) {
		
		return extractClaim(token, Claims::getSubject);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		
		return (username.equals(userDetails.getUsername()) && !isTokenExpaired(token));
	}

	private boolean isTokenExpaired(String token) {
		return extractExparation(token).before(new Date());
	}

	private Date extractExparation(String token) {
		
		return extractClaim(token, Claims::getExpiration);
	}


}
