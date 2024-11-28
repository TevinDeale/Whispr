package com.tevind.whispr.repository;

import com.tevind.whispr.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
