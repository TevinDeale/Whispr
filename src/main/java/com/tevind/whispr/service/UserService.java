package com.tevind.whispr.service;

import com.tevind.whispr.dto.entity.UserDto;
import com.tevind.whispr.enums.AccountRoles;
import com.tevind.whispr.exception.DuplicateAttributeException;
import com.tevind.whispr.exception.UserNotFoundException;
import com.tevind.whispr.model.User;
import com.tevind.whispr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public User createUser(UserDto dto) {
        log.debug("Creating user with username: {}", dto.getUserName());
        isDuplicateAttribute(dto.getUserName(), dto.getEmail());

        User createdUser = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .accountRoles(new HashSet<>(List.of(AccountRoles.USER)))
                .build();

        log.debug("Successfully created user with username: {}", createdUser.getUserName());
        return repository.save(createdUser);
    }

    public User findById(UUID userId) {
        log.debug("Finding user with ID: {}", userId);
        User foundUser = repository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID: {} was not found", userId);
                    return new UserNotFoundException("User with Id: " + userId + " does not exist");
                });

        log.debug("Found user with ID: {}",userId);
        return foundUser;
    }

    public User findByUsername(String username) {
        log.debug("Finding user with username: {}", username);
        User foundUser = repository.findByUserName(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new UserNotFoundException("User with username: " + username + " does not exist");
                });

        log.debug("Found user with username: {}", username);
        return foundUser;
    }

    public User findByEmail(String email) {
        log.debug("Finding user with email: {}", email);
        User foundUser = repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UserNotFoundException("User with email: " + email + " does not exist");
                });

        log.debug("Found user with email: {}", email);
        return foundUser;
    }

    private void isDuplicateAttribute(String username, String email) {
        log.debug("Checking for Duplicate Username or Email");
        if (repository.existsByUserName(username)) {
            log.warn("Username already taken: {}", username);
            throw new DuplicateAttributeException("Username already taken");
        }

        if (repository.existsByEmail(email)) {
            log.warn("Email already taken: {}", email);
            throw new DuplicateAttributeException("Email already being used by another account");
        }

        log.debug("Username and email is valid");
    }

}
