package com.klu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.klu.entity.AppUser;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

	Optional<AppUser> findByUsername(String username);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}
