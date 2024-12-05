package com.tevind.whispr.model;

import com.tevind.whispr.enums.ProfileStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Data
@Table(name = "profiles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profile_id")
    private UUID profileId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private ProfileStatus status;

    @Column(nullable = false)
    private LocalDateTime lastSeen;

    @ManyToMany(mappedBy = "participants")
    private Set<Thread> threads = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof  Profile profile)) return false;
        return profileId != null && profileId.equals(profile.profileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileId);
    }
}
