package com.tevind.whispr.security;

import com.tevind.whispr.dto.converter.DtoConverter;
import com.tevind.whispr.dto.responses.UserResponseDto;
import com.tevind.whispr.model.User;
import com.tevind.whispr.service.ProfileService;
import com.tevind.whispr.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;
    private final ProfileService profileService;

    public CustomUserDetailsService(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.debug("Loading user: {} in custom user details", username);
        User user = userService.findByUsername(username);

        log.debug("Converting user: {} to a UserResponseDto", user.getUserName());
        UserResponseDto responseDto = DtoConverter.toUserResponse(user);

        log.debug("Returning a new UserDetails object");
        return new CustomUserDetails(responseDto, profileService);
    }
}
