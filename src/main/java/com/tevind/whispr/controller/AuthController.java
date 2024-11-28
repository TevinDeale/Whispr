package com.tevind.whispr.controller;

import com.tevind.whispr.dto.converter.DtoConverter;
import com.tevind.whispr.dto.entity.LoginDto;
import com.tevind.whispr.dto.entity.TokenDto;
import com.tevind.whispr.dto.responses.AuthResponseDto;
import com.tevind.whispr.dto.responses.BaseResponseDto;
import com.tevind.whispr.exception.AuthenticationErrorException;
import com.tevind.whispr.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
        TokenDto tokenDto = authService.login(loginDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(createdAuthResponse(tokenDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponseDto> logout(HttpServletRequest request) {
        String rawToken = getToken(request);

        if (Boolean.TRUE.equals(authService.logout(rawToken))) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new BaseResponseDto(LocalDateTime.now(), "Logout successful"));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new BaseResponseDto(LocalDateTime.now(), "Logout not successful"));

    }

    private AuthResponseDto createdAuthResponse(TokenDto tokenDto) {
        return DtoConverter.toAuthResponse(tokenDto.getToken(), tokenDto.getExpiration());
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer")) {
            return token.substring(7);
        }

        log.warn("Token is not preset or invalid");
        throw new AuthenticationErrorException("Token is not present or invalid");
    }
}
