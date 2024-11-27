package com.tevind.whispr.controller;

import com.tevind.whispr.dto.responses.UserResponseDto;
import com.tevind.whispr.exception.AuthenticationErrorException;
import com.tevind.whispr.model.User;
import com.tevind.whispr.security.CustomUserDetails;
import com.tevind.whispr.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getUser() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getUserResponse());
    }

    private UserResponseDto getUserResponse() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUser();
        } catch (Exception err) {
            log.debug("Error getting user from security context");
            throw new AuthenticationErrorException("Error getting user from security context");
        }
    }
}
