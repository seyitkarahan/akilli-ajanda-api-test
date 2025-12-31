package com.seyitkarahan.akilli_ajanda_api.repository;

import com.seyitkarahan.akilli_ajanda_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
