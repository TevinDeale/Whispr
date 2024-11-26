package com.tevind.whispr.dto.converter;

import com.tevind.whispr.dto.responses.ErrorResponseDto;
import com.tevind.whispr.dto.responses.ProfileResponseDto;
import com.tevind.whispr.dto.responses.UserResponseDto;
import com.tevind.whispr.model.Profile;
import com.tevind.whispr.model.User;

import java.time.LocalDate;

public class DtoConverter {

    public static ErrorResponseDto toErrorResponse(String message, String exceptionName, String path, int statusCode) {
        return ErrorResponseDto.builder()
                .timestamp(LocalDate.now())
                .message(message)
                .exceptionName(exceptionName)
                .path(path)
                .statusCode(statusCode)
                .build();
    }

    public static ProfileResponseDto toProfileResponse(Profile profile) {
        return ProfileResponseDto.builder()
                .timestamp(LocalDate.now())
                .message("Profile Details")
                .profileId(profile.getProfileId())
                .displayName(profile.getDisplayName())
                .status(profile.getStatus())
                .lastSeen(profile.getLastSeen())
                .build();
    }

    public static UserResponseDto toUserResponse(User user) {
        return UserResponseDto.builder()
                .timestamp(LocalDate.now())
                .message("User Details")
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUserName())
                .roles(user.getAccountRoles())
                .created(user.getCreated())
                .lastUpdated(user.getUpdated())
                .isActive(user.isActive())
                .build();
    }
}
