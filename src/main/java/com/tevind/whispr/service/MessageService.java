package com.tevind.whispr.service;

import com.tevind.whispr.dto.entity.MessageDto;
import com.tevind.whispr.model.Message;
import com.tevind.whispr.model.Profile;
import com.tevind.whispr.model.Thread;
import com.tevind.whispr.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final ProfileService profileService;
    private final ThreadService threadService;

    public MessageService(MessageRepository messageRepository, ProfileService profileService, ThreadService threadService) {
        this.messageRepository = messageRepository;
        this.profileService = profileService;
        this.threadService = threadService;
    }

    public Message createMessage(MessageDto dto) {

        Profile profile = profileService.findById(dto.getUserId());
        Thread thread = threadService.findById(dto.getThreadId());

        Message newMessage = Message.builder()
                .thread(thread)
                .profile(profile)
                .content(dto.getContent())
                .build();

        return messageRepository.save(newMessage);
    }
}
