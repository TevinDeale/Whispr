package com.tevind.whispr.controller;

import com.tevind.whispr.dto.converter.DtoConverter;
import com.tevind.whispr.dto.entity.LoginDto;
import com.tevind.whispr.dto.entity.TokenDto;
import com.tevind.whispr.dto.responses.AuthResponseDto;
import com.tevind.whispr.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private AuthResponseDto createdAuthResponse(TokenDto tokenDto) {
        return DtoConverter.toAuthResponse(tokenDto.getToken(), tokenDto.getExpiration());
    }
}
