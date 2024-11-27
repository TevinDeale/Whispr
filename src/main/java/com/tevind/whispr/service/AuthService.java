package com.tevind.whispr.service;

import com.tevind.whispr.dto.entity.LoginDto;
import com.tevind.whispr.dto.entity.TokenDto;
import com.tevind.whispr.exception.AccountNotActiveException;
import com.tevind.whispr.exception.AuthenticationErrorException;
import com.tevind.whispr.exception.UserNotFoundException;
import com.tevind.whispr.model.User;
import com.tevind.whispr.security.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@Slf4j
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordEncoder encoder;
    final String invalidCredentials = "Invalid credentials";

    public AuthService(JwtUtil jwtUtil, UserService userService, PasswordEncoder encoder) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.encoder = encoder;
    }

    public TokenDto login(LoginDto loginDto) {
        String loginUsername = loginDto.getUsername();
        String loginPassword = loginDto.getPassword();

        try {
            log.debug("Processing login attempt");
            User user = userService.findByUsername(loginUsername);

            if (!user.isActive()) {
                log.warn("Inactive account login attempt");
                throw new AccountNotActiveException("Access Denied: Account is not active, please contact support");
            }

            String userPassword = user.getPassword();

            if (!isValidPassword(loginPassword, userPassword)) {
                log.warn("Failed login attempt");
                throw new AuthenticationErrorException(invalidCredentials);
            }

            String token = jwtUtil.generateToken(
                    user.getUserName(),
                    user.getUserId(),
                    user.getAccountRoles());

            return createTokenDto(token);

        } catch (AccountNotActiveException err) {
            log.warn("Authentication failed: {}", err.getMessage());
            throw err;
        } catch (UserNotFoundException | AuthenticationErrorException err) {
            log.warn("Authentication failed: {}", err.getMessage());
            throw new AuthenticationErrorException(invalidCredentials);
        } catch (Exception err) {
            log.error("Unexpected error during login attempt");
            throw new AuthenticationErrorException("Authentication failed");
        }
    }

    private TokenDto createTokenDto(String token) {
        Date tokenExpiration = jwtUtil.getExpiration(token);
        LocalDateTime expiration = LocalDateTime.ofInstant(tokenExpiration.toInstant(), ZoneId.systemDefault());

        return new TokenDto(token, expiration);
    }

    private boolean isValidPassword(String rawPassword, String encodedPassword) {
        try {
            return encoder.matches(rawPassword, encodedPassword);
        } catch (Exception err) {
            log.error("Error during password comparison", err);
            return false;
        }
    }
}
