package com.tevind.whispr.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long messageId;

    @ManyToOne
    @JoinColumn(name = "thread_id")
    private Thread thread;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @CreationTimestamp
    private LocalDateTime created;

    private LocalDateTime deliveredAt;
}
