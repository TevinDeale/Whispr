package com.tevind.whispr.dto.responses;

import com.tevind.whispr.enums.AccountRoles;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    private UUID userId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Set<AccountRoles> roles;
    private LocalDate created;
    private LocalDate lastUpdated;
    private Boolean isActive;
}
