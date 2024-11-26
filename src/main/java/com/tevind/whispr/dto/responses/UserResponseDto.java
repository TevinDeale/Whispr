package com.tevind.whispr.dto.responses;

import com.tevind.whispr.enums.AccountRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto extends BaseResponseDto{

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
