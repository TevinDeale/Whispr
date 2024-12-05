package com.tevind.whispr.controller;

import com.tevind.whispr.dto.converter.DtoConverter;
import com.tevind.whispr.dto.entity.ThreadCreationDto;
import com.tevind.whispr.dto.entity.ThreadDto;
import com.tevind.whispr.dto.responses.UserResponseDto;
import com.tevind.whispr.model.Profile;
import com.tevind.whispr.model.Thread;
import com.tevind.whispr.security.CustomUserDetails;
import com.tevind.whispr.service.ProfileService;
import com.tevind.whispr.service.ThreadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/thread")
@Slf4j
public class ThreadController {

    private final ThreadService threadService;

    public ThreadController(ThreadService threadService) {
        this.threadService = threadService;
    }

    @GetMapping("/{threadId}")
    public ResponseEntity<ThreadDto> getThread(@PathVariable String threadId) {
        log.debug("Getting thread {}", threadId);
        Thread thread = threadService.getThread(threadId, getProfile());


        log.debug("Returning ThreadDto from thread {}", thread.getThreadId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DtoConverter.toThreadDto(thread));
    }

    @PostMapping("/create")
    public ResponseEntity<Thread> createThread(@RequestBody ThreadCreationDto dto) {

        Thread thread = threadService.createThread(getProfile(), dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(thread);
    }

    private Profile getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getProfile();
    }
}
