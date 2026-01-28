package com.dev.monkey_dev.audit;

import java.util.Map;

public record AuditLogEvent(
        Long actorUserId,
        String action,
        String entityType,
        Long entityId,
        String description,
        String requestId,
        String ipAddress,
        String userAgent,
        Boolean success,
        Map<String, Object> metadata
) {}
