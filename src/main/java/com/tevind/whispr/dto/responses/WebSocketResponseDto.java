package com.tevind.whispr.dto.responses;
import com.tevind.whispr.enums.WebSocketMessageType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketResponseDto {
    private WebSocketMessageType type;
    private Object message;
}
