package com.tevind.whispr.security;

import com.tevind.whispr.dto.responses.UserResponseDto;
import com.tevind.whispr.model.Profile;
import com.tevind.whispr.service.ProfileService;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetails implements UserDetails {

    @Getter
    private final UserResponseDto user;
    private final ProfileService profileService;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(UserResponseDto user, ProfileService profileService) {
        this.user = user;
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
        this.profileService = profileService;
    }

    public Profile getProfile() {
        return profileService.findById(user.getUserId());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return user.getIsActive();
    }
}
