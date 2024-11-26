package com.tevind.whispr.dto.responses;

import com.tevind.whispr.enums.ProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDto extends BaseResponseDto{

    private UUID profileId;

    private String displayName;

    private ProfileStatus status;

    private LocalDateTime lastSeen;
}
