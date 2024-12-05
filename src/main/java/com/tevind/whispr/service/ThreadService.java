package com.tevind.whispr.service;

import com.tevind.whispr.dto.entity.ThreadCreationDto;
import com.tevind.whispr.enums.ThreadRoles;
import com.tevind.whispr.exception.ThreadErrorException;
import com.tevind.whispr.exception.ThreadNotFoundException;
import com.tevind.whispr.model.Profile;
import com.tevind.whispr.model.Thread;
import com.tevind.whispr.repository.ThreadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class ThreadService {

    private final ThreadRepository threadRepository;

    public ThreadService(ThreadRepository threadRepository) {
        this.threadRepository = threadRepository;
    }

    public Thread createThread(Profile profile, ThreadCreationDto dto) {
        try {
            String code = generateInviteCode();

            Thread newThread = Thread.builder()
                    .threadName(dto.getThreadName())
                    .inviteCode(code)
                    .build();

            Thread savedThread = threadRepository.save(newThread);

            savedThread.addParticipant(profile);
            savedThread.addParticipantRole(profile, ThreadRoles.OWNER);

            return threadRepository.save(savedThread);
        } catch (Exception err) {
            log.error("Error creating thread", err);
            throw new ThreadErrorException("Error creating thread. Please try again later");
        }
    }

    public Boolean joinThread(Profile profile, String code) {
        log.debug("User: {} attempting to join thread with code: {}", profile.getDisplayName(), code);
        Thread thread = threadRepository.findByInviteCode(code)
                .orElseThrow(() -> {
                    log.warn("Invite code: {} is not valid", code);
                    return new ThreadNotFoundException("Thread not found with invite code " + code);
                });

        log.debug("Thread found with code: {}", code);
        thread.addParticipant(profile);
        thread.addParticipantRole(profile, ThreadRoles.USER);

        Thread savedThread = threadRepository.save(thread);

        log.debug("User {} join thread with code {}", profile.getDisplayName(), code);
        return isParticipant(savedThread, profile);
    }

    public Thread findById(String threadId) {
        log.debug("Finding thread by ID: {}", threadId);
        Thread thread = threadRepository.findById(UUID.fromString(threadId))
                .orElseThrow(() -> {
                    log.warn("Thread with ID {} does not exist", threadId);
                    return new ThreadNotFoundException("Thread not found with ID: " + threadId);
                });

        log.debug("Found thread with ID: {}", thread.getThreadId());
        return thread;
    }

    public Thread getThread(String threadId, Profile profile) {
        log.debug("Getting thread {} for user {}", threadId, profile.getDisplayName());
        Thread thread = findById(threadId);

        if (Boolean.FALSE.equals(isParticipant(thread, profile))) {
            log.warn("User {} is not a participant of thread {}", profile.getDisplayName(), threadId);
            throw new ThreadErrorException("User is not apart of thread");
        }

        log.debug("Return thread {} for user {}", thread.getThreadId(), profile.getDisplayName());
        return thread;
    }

    private Boolean isParticipant(Thread thread, Profile profile) {
        log.debug("Checking if user {} is a participant of thread {}", profile.getDisplayName(), thread.getThreadId());
        UUID threadId = thread.getThreadId();
        Boolean isMember = threadRepository.existsByThreadIdAndParticipantsContains(threadId, profile);

        log.debug("User {} is member of thread {}: {}", profile.getDisplayName(), thread.getThreadId(), isMember);
        return isMember;
    }

    private String generateInviteCode() {
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numeric = "1234567890";

        String alphaNumeric = alpha + numeric;

        StringBuilder code = new StringBuilder(6);

        for (int x = 0; x < 6; x++) {
            int randCharIndex = (int) (alphaNumeric.length() * Math.random());

            code.append(alphaNumeric.charAt(randCharIndex));
        }

        return code.toString();
    }
}
