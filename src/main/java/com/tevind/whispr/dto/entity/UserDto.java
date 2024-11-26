package com.tevind.whispr.dto.entity;

import com.tevind.whispr.enums.AccountRoles;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotNull(message = "First name cannot be blank")
    @Size(min = 1, max = 15)
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "Name must contain only letters, spaces, and hyphens")
    private String firstName;

    @NotNull(message = "Last name cannot be blank")
    @Size(min = 1, max = 15)
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "Name must contain only letters, spaces, and hyphens")
    private String lastName;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 5, max = 15, message = "Username has to be between 8 and 15 characters.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
    message = "Password must contain 1 letter, 1 number, 1 special character and must be at least 8 characters")
    private String userName;

    @NotBlank(message = "Email cannot be blank")
    @Email
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 5, max= 20, message = "Password must be between 5 and 20 characters.")
    @Pattern(regexp = "^[a-zA-z]")
    private String password;

    @Enumerated(EnumType.STRING)
    private Set<AccountRoles> accountRoles;
}
