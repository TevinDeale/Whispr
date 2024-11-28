package com.tevind.whispr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ThreadRepository extends JpaRepository<Thread, UUID> {
}
