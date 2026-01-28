package com.dev.monkey_dev.domain.respository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.monkey_dev.domain.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    long deleteByCreatedAtBefore(LocalDateTime cutoff);
}
