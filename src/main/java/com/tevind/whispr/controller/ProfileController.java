package com.tevind.whispr.controller;

import com.tevind.whispr.dto.converter.DtoConverter;
import com.tevind.whispr.dto.responses.ProfileResponseDto;
import com.tevind.whispr.dto.responses.UserResponseDto;
import com.tevind.whispr.exception.AuthenticationErrorException;
import com.tevind.whispr.model.Profile;
import com.tevind.whispr.security.CustomUserDetails;
import com.tevind.whispr.service.ProfileService;
import com.tevind.whispr.service.UserService;
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
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponseDto> getUser() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getProfileResponse());
    }

    private ProfileResponseDto getProfileResponse() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UserResponseDto userResponseDto = userDetails.getUser();
            Profile profile = profileService.findByDisplayName(userResponseDto.getUsername());
            return DtoConverter.toProfileResponse(profile, userResponseDto);
        } catch (Exception err) {
            log.debug("Error getting user from security context");
            throw new AuthenticationErrorException("Error getting user from security context");
        }
    }
}
