package com.tevind.whispr.service;

import com.tevind.whispr.enums.ProfileStatus;
import com.tevind.whispr.exception.ProfileNotFoundException;
import com.tevind.whispr.model.Profile;
import com.tevind.whispr.model.User;
import com.tevind.whispr.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final ProfileRepository profileRepository;

    @Transactional
    public Profile createProfile(User user) {
        log.debug("Creating profile with username: {}", user.getUserName());

        Profile profile = Profile.builder()
                .user(user)
                .displayName(user.getUserName())
                .status(ProfileStatus.OFFLINE)
                .lastSeen(LocalDateTime.now())
                .build();

        Profile savedProfile = profileRepository.save(profile);

        log.debug("Successfully created profile with displayName: {}", savedProfile.getDisplayName());
        return savedProfile;
    }

    public Profile findByDisplayName(String displayName) {
        log.debug("Finding profile by displayName: {}", displayName);

        Profile foundProfile = profileRepository.findByDisplayName(displayName)
                .orElseThrow(() -> {
                    log.warn("Profile not found with displayName: {}", displayName);
                    return new ProfileNotFoundException("Profile not found with displayName: " + displayName);
                });

        log.debug("Returning found profile with displayName: {}", foundProfile.getDisplayName());
        return foundProfile;
    }
}
