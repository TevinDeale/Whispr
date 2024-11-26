package com.tevind.whispr.model;

import com.tevind.whispr.enums.AccountRoles;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false)
    @NotNull(message = "First name cannot be blank")
    @Size(min = 1, max = 15)
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "Name must contain only letters, spaces, and hyphens")
    private String firstName;

    @Column(nullable = false)
    @NotNull(message = "Last name cannot be blank")
    @Size(min = 1, max = 15)
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "Name must contain only letters, spaces, and hyphens")
    private String lastName;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 5, max = 15, message = "Username has to be between 5 and 15 characters.")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]+$", message = "Username must start with a letter and contain only letters and numbers")
    private String userName;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Email cannot be blank")
    @Email
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Password cannot be blank")
    private String password;

    @Column(nullable = false)
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccountRoles> accountRoles;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDate created;

    @UpdateTimestamp
    private LocalDate updated;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}