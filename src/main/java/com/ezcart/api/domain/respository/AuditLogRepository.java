package com.ezcart.api.domain.respository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ezcart.api.domain.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    long deleteByCreatedAtBefore(LocalDateTime cutoff);
}

