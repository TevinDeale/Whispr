package com.tevind.whispr.security;

import com.tevind.whispr.dto.converter.DtoConverter;
import com.tevind.whispr.dto.responses.UserResponseDto;
import com.tevind.whispr.model.User;
import com.tevind.whispr.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.debug("Loading user: {} in custom user details", username);
        User user = userService.findByUsername(username);

        log.debug("Converting user: {} to a UserResponseDto", user.getUserName());
        UserResponseDto responseDto = DtoConverter.toUserResponse(user);

        log.debug("Returning a new UserDetails object");
        return new CustomUserDetails(responseDto);
    }
}
