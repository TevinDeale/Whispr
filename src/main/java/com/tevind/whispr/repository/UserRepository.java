package com.tevind.whispr.repository;

import com.tevind.whispr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByUserName(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUserName(String username);
    Optional<User> findByEmail(String email);
}
