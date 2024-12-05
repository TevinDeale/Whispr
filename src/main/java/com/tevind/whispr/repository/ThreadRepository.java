package com.tevind.whispr.repository;

import com.tevind.whispr.model.Profile;
import com.tevind.whispr.model.Thread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ThreadRepository extends JpaRepository<Thread, UUID> {

    boolean existsByThreadName(String name);
    Optional<Thread> findByThreadName(String name);
    Optional<Thread> findByInviteCode(String code);
    Boolean existsByThreadIdAndParticipantsContains(UUID threadId, Profile profile);
}
