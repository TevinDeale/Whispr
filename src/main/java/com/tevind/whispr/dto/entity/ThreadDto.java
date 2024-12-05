package com.tevind.whispr.dto.entity;

import com.tevind.whispr.enums.ThreadRoles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadDto {
    private UUID threadId;
    private String threadName;
    private String inviteCode;
    private Set<String> participants;
    private Map<String, ThreadRoles> participantRoles;
}
