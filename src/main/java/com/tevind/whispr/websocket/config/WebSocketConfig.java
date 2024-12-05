package com.tevind.whispr.websocket.config;

import com.tevind.whispr.service.MessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final RedisTemplate<String, String> stringRedisTemplate;
    private final MessageService messageService;
    private final WebSocketInterceptor interceptor;

    public WebSocketConfig(RedisTemplate<String, String> stringRedisTemplate, MessageService messageService, WebSocketInterceptor interceptor) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.messageService = messageService;
        this.interceptor = interceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/whispr")
                .setAllowedOrigins("*")
                .addInterceptors(interceptor);
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new ChatHandler(stringRedisTemplate, messageService);
    }
}
