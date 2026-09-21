package com.klu.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klu.dto.AuthResponse;
import com.klu.dto.LoginRequest;
import com.klu.dto.RegisterRequest;
import com.klu.dto.UserResponse;
import com.klu.entity.AppUser;
import com.klu.exception.BusinessRuleException;
import com.klu.exception.ResourceNotFoundException;
import com.klu.repository.UserRepository;
import com.klu.security.JwtUtil;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new BusinessRuleException("Username already taken: " + request.getUsername());
		}
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessRuleException("Email already registered: " + request.getEmail());
		}

		AppUser user = new AppUser();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(request.getRole() == null || request.getRole().isBlank() ? "ROLE_USER" : request.getRole());

		AppUser saved = userRepository.save(user);
		String token = jwtUtil.generateToken(saved.getId(), saved.getUsername(), saved.getRole());
		return new AuthResponse(token, saved.getId(), saved.getUsername(), saved.getRole(), jwtUtil.getExpirationMs());
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		AppUser user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new BusinessRuleException("Invalid username or password"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BusinessRuleException("Invalid username or password");
		}

		String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
		return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole(), jwtUtil.getExpirationMs());
	}

	@Transactional(readOnly = true)
	public UserResponse getUser(Long id) {
		AppUser user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
		return toResponse(user);
	}

	@Transactional(readOnly = true)
	public List<UserResponse> getAllUsers() {
		return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
	}

	public boolean validateToken(String token) {
		return jwtUtil.isValid(token);
	}

	private UserResponse toResponse(AppUser user) {
		return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
	}
}
