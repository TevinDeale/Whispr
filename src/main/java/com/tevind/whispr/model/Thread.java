package com.tevind.whispr.model;

import com.tevind.whispr.enums.ThreadRoles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "chat_threads")
@Entity
public class Thread {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "thread_id")
    private UUID threadId;

    @Column(name = "thread_name", nullable = false)
    private String threadName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "thread_id")
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "thread_participants",
            joinColumns = @JoinColumn(name = "thread_id"),
            inverseJoinColumns = @JoinColumn(name = "profileId")
    )
    @Column(nullable = false)
    @Builder.Default
    private Set<Profile> participants = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "thread_participant_roles",
            joinColumns = @JoinColumn(name = "thread_id")
    )
    @MapKeyJoinColumn(name = "profileId")
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Map<Profile, ThreadRoles> participantRoles = new HashMap<>();

    @Column(nullable = false)
    private String inviteCode;

    @Column(nullable = false)
    @Builder.Default
    private int maxParticipants = 4;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Thread thread)) return false;
        return threadId != null && threadId.equals(thread.threadId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(threadId);
    }

    public void addParticipant(Profile profile) {
        participants.add(profile);
    }

    public void addParticipantRole(Profile profile, ThreadRoles role) {
        participantRoles.put(profile, role);
    }
}
