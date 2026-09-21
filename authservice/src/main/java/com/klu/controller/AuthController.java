package com.klu.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klu.dto.AuthResponse;
import com.klu.dto.LoginRequest;
import com.klu.dto.RegisterRequest;
import com.klu.dto.UserResponse;
import com.klu.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@GetMapping("/validate")
	public ResponseEntity<Map<String, Object>> validate(@RequestHeader("Authorization") String authorization) {
		String token = authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
		boolean valid = authService.validateToken(token);
		return ResponseEntity.ok(Map.of("valid", valid));
	}

	@GetMapping("/users")
	public ResponseEntity<List<UserResponse>> all() {
		return ResponseEntity.ok(authService.getAllUsers());
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<UserResponse> byId(@PathVariable Long id) {
		return ResponseEntity.ok(authService.getUser(id));
	}
}
