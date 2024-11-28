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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "thread_id")
    private List<Message> messages = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "thread_participants",
            joinColumns = @JoinColumn(name = "thread_id"),
            inverseJoinColumns = @JoinColumn(name = "profileId")
    )
    @Column(nullable = false)
    private Set<Profile> participants = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "thread_participant_roles",
            joinColumns = @JoinColumn(name = "thread_id")
    )
    @MapKeyJoinColumn(name = "profileId")
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Map<Profile, ThreadRoles> participantRoles = new HashMap<>();

    @Column(nullable = false)
    private String inviteCode;

    @Column(nullable = false)
    @Builder.Default
    private int maxParticipants = 2;
}
