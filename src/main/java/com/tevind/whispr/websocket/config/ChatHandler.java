package com.tevind.whispr.websocket.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tevind.whispr.dto.entity.MessageDto;
import com.tevind.whispr.dto.entity.SessionDto;
import com.tevind.whispr.dto.responses.WebSocketResponseDto;
import com.tevind.whispr.enums.MessageStatus;
import com.tevind.whispr.enums.WebSocketMessageType;
import com.tevind.whispr.model.Message;
import com.tevind.whispr.model.WebSocketMessage;
import com.tevind.whispr.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ChatHandler extends TextWebSocketHandler {

    private final RedisTemplate<String, String> redisTemplate;
    private final ConcurrentHashMap<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
    private static final String WS_PREFIX = "websocket:session:";
    private static final String THREAD_SESSION = "thread:session:";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MessageService messageService;

    public ChatHandler(RedisTemplate<String, String> redisTemplate, MessageService messageService) {
        this.redisTemplate = redisTemplate;
        this.messageService = messageService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            String userId = session.getAttributes().get("userId").toString();
            SessionDto sessionDto = new SessionDto(
                    session.getId(),
                    userId
            );

            redisTemplate.opsForValue().set(
                    WS_PREFIX + userId,
                    new ObjectMapper().writeValueAsString(sessionDto)
            );

            activeSessions.put(session.getId(), session);

            log.info("WebSocket connection established for user: {}", userId);
        } catch (Exception err) {
            log.error("Error establishing WebSocket connection: ", err);
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();
            WebSocketMessage wsMessage = objectMapper.readValue(payload, WebSocketMessage.class);

            switch (wsMessage.getMessageType()) {
                case SUBSCRIBE_THREAD -> handleThreadSubscription(session, wsMessage.getThreadId());
                case MESSAGE -> handleChatMessage(session, wsMessage.getMessage());
                case UNSUBSCRIBE_THREAD -> handleThreadUnsubscription(session, wsMessage.getThreadId());
            }

        } catch (Exception err) {
            log.error("Error handling message: ", err);
        }
    }

    private void handleThreadSubscription(WebSocketSession session, String threadId) {
        String sessionId = session.getId();

        redisTemplate.opsForSet().add(THREAD_SESSION + threadId, sessionId);
        log.info("Session {} subscribed to thread {}", sessionId, threadId);
    }

    private void handleThreadUnsubscription(WebSocketSession session, String threadId) {
        String sessionId = session.getId();

        redisTemplate.opsForSet().remove(THREAD_SESSION + threadId, sessionId);
        log.info("Session {} unsubscribed from thread {}", sessionId, threadId);
    }

    private void handleChatMessage(WebSocketSession session, MessageDto messageDto) {
        UUID userId = UUID.fromString(
                session.getAttributes().get("userId").toString());

        messageDto.setUserId(userId);

        Message savedMessage = messageService.createMessage(messageDto);

        WebSocketResponseDto threadUpdateEvent = new WebSocketResponseDto(
                WebSocketMessageType.THREAD_UPDATE,
                Map.of(
                        "threadId", savedMessage.getThread().getThreadId(),
                        "message", messageDto
                )
        );

        broadcastToThread(savedMessage.getThread().getThreadId(), threadUpdateEvent);

        sendToSession(session, new WebSocketResponseDto(WebSocketMessageType.SENT, messageDto));
    }

    private void broadcastToThread(UUID threadId, WebSocketResponseDto responseDto) {
        try {
            String messageJson = objectMapper.writeValueAsString(responseDto);
            TextMessage textMessage = new TextMessage(messageJson);

            Set<String> subscribedSessions = redisTemplate.opsForSet().members(THREAD_SESSION + threadId);

            if (subscribedSessions != null) {
                for (String sessionId : subscribedSessions) {

                    WebSocketSession session = activeSessions.get(sessionId);

                    if (session != null && session.isOpen()) {
                        try {
                            session.sendMessage(textMessage);
                        } catch (IOException e) {
                            log.error("Failed to send message to session {}", sessionId, e);
                        }
                    } else {

                        log.warn("Session {} no longer active, removing from subscriptions", sessionId);
                        redisTemplate.opsForSet().remove(THREAD_SESSION + threadId, sessionId);
                    }
                }
            }
        } catch (Exception err) {
            log.error("Error broadcasting message to thread {}: ", threadId, err);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        try {
            String userId = session.getAttributes().get("userId").toString();
            String sessionId = session.getId();

            Set<String> threadKeys = redisTemplate.keys(THREAD_SESSION + "*");
            if (threadKeys != null) {
                for (String threadKey : threadKeys) {
                    redisTemplate.opsForSet().remove(threadKey, sessionId);
                }
            }

            redisTemplate.delete(WS_PREFIX + userId);
            log.info("WebSocket connection closed for user: {}", userId);
        } catch (Exception e) {
            log.error("Error closing WebSocket connection: ", e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Transport error: ", exception);
        session.close(CloseStatus.SERVER_ERROR);
    }

    private void sendToSession(WebSocketSession session, WebSocketResponseDto response) {
        try {
            String messageJson = objectMapper.writeValueAsString(response);
            session.sendMessage(new TextMessage(messageJson));
        } catch (IOException e) {
            log.error("Error sending message to session {}", session.getId(), e);
        }
    }
}
