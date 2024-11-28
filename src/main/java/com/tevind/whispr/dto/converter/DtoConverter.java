package com.tevind.whispr.dto.converter;

import com.tevind.whispr.dto.entity.UserDto;
import com.tevind.whispr.dto.responses.AuthResponseDto;
import com.tevind.whispr.dto.responses.ErrorResponseDto;
import com.tevind.whispr.dto.responses.ProfileResponseDto;
import com.tevind.whispr.dto.responses.UserResponseDto;
import com.tevind.whispr.enums.AccountRoles;
import com.tevind.whispr.model.Profile;
import com.tevind.whispr.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

public class DtoConverter {

    public static ErrorResponseDto toErrorResponse(String message, String path, int statusCode) {
        return ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .message(message)
                .path(path)
                .statusCode(statusCode)
                .build();
    }

    public static ProfileResponseDto toProfileResponse(Profile profile, UserResponseDto userResponseDto) {
        return ProfileResponseDto.builder()
                .profileId(profile.getProfileId())
                .displayName(profile.getDisplayName())
                .status(profile.getStatus())
                .lastSeen(profile.getLastSeen())
                .userResponseDto(userResponseDto)
                .build();
    }

    public static UserResponseDto toUserResponse(User user) {
        return UserResponseDto.builder()
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

    public static User toUser(UserDto dto) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .accountRoles(new HashSet<>(List.of(AccountRoles.USER)))
                .build();
    }

    public static AuthResponseDto toAuthResponse(String token, LocalDateTime expiration) {
        return AuthResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .message("Authentication Successful")
                .token(token)
                .expiration(expiration)
                .build();
    }

}
