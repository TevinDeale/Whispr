package com.tevind.whispr.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
    private String sessionId;
    private String userId;
}
