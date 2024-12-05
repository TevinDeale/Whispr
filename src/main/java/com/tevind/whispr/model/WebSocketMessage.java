package com.tevind.whispr.model;

import com.tevind.whispr.dto.entity.MessageDto;
import com.tevind.whispr.enums.WebSocketMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    private WebSocketMessageType messageType;
    private String threadId;
    private MessageDto message;
}
