package com.klu.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	@Value("${bidvelocity.jwt.secret}")
	private String secret;

	@Value("${bidvelocity.jwt.expiration-ms}")
	private long expirationMs;

	private SecretKey key() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(Long userId, String username, String role) {
		Date now = new Date();
		return Jwts.builder()
				.subject(username)
				.claim("userId", userId)
				.claim("role", role)
				.issuedAt(now)
				.expiration(new Date(now.getTime() + expirationMs))
				.signWith(key())
				.compact();
	}

	public Claims parse(String token) {
		return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
	}

	public boolean isValid(String token) {
		try {
			parse(token);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public String getUsername(String token) {
		return parse(token).getSubject();
	}

	public Long getUserId(String token) {
		Object v = parse(token).get("userId");
		return v == null ? null : Long.valueOf(v.toString());
	}

	public String getRole(String token) {
		Object v = parse(token).get("role");
		return v == null ? "ROLE_USER" : v.toString();
	}

	public long getExpirationMs() {
		return expirationMs;
	}
}
