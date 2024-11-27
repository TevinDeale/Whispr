package com.tevind.whispr.dto.responses;

import com.tevind.whispr.enums.ProfileStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDto{

    private UUID profileId;

    private String displayName;

    private ProfileStatus status;

    private LocalDateTime lastSeen;

    private UserResponseDto userResponseDto;
}
