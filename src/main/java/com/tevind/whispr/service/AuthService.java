package com.tevind.whispr.service;

import com.tevind.whispr.dto.entity.LoginDto;
import com.tevind.whispr.dto.entity.TokenDto;
import com.tevind.whispr.exception.AccountNotActiveException;
import com.tevind.whispr.exception.AuthenticationErrorException;
import com.tevind.whispr.exception.UserNotFoundException;
import com.tevind.whispr.model.User;
import com.tevind.whispr.security.jwt.JwtUtil;
import com.tevind.whispr.security.session.Session;
import com.tevind.whispr.security.session.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordEncoder encoder;
    final String invalidCredentials = "Invalid credentials";
    private final SessionService sessionService;

    public AuthService(JwtUtil jwtUtil, UserService userService, PasswordEncoder encoder, SessionService sessionService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.encoder = encoder;
        this.sessionService = sessionService;
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

            Session session = new Session(user.getUserId(), token);

            sessionService.createSession(session);

            return createTokenDto(token);

        } catch (AccountNotActiveException err) {
            log.warn("Authentication failed: {}", err.getMessage());
            throw err;
        } catch (UserNotFoundException | AuthenticationErrorException err) {
            log.warn("Authentication failed: {}", err.getMessage());
            throw new AuthenticationErrorException(invalidCredentials);
        } catch (Exception err) {
            log.error("Unexpected error during login attempt", err);
            throw new AuthenticationErrorException("Authentication failed, please try again later");
        }
    }

    public Boolean logout(String token) {
        UUID userId = jwtUtil.getUserId(token);

        Boolean successLogout = sessionService.invalidateSession(userId.toString());

        if (successLogout.equals(Boolean.FALSE)) {
            log.error("Error logging user out");
            throw new AuthenticationErrorException("Error logging user out");
        }

        return Boolean.TRUE;
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
