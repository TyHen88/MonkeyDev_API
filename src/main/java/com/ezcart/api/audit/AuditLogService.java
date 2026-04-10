package com.ezcart.api.audit;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ezcart.api.domain.entity.AuditLog;
import com.ezcart.api.domain.respository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void save(AuditLogEvent event) {
        if (event == null) {
            return;
        }
        AuditLog log = AuditLog.builder()
                .actorUserId(event.actorUserId())
                .action(event.action())
                .entityType(event.entityType())
                .entityId(event.entityId())
                .description(event.description())
                .requestId(event.requestId())
                .ipAddress(event.ipAddress())
                .userAgent(event.userAgent())
                .success(event.success() != null ? event.success() : Boolean.TRUE)
                .metadata(event.metadata())
                .build();
        auditLogRepository.save(log);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public long deleteOlderThan(LocalDateTime cutoff) {
        return auditLogRepository.deleteByCreatedAtBefore(cutoff);
    }
}

