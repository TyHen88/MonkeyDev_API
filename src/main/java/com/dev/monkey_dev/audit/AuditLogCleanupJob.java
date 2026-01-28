package com.dev.monkey_dev.audit;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dev.monkey_dev.logging.AppLogManager;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditLogCleanupJob {

    private final AuditLogService auditLogService;

    @Value("${app.audit.retention-days:90}")
    private int retentionDays;

    @Scheduled(cron = "${app.audit.cleanup-cron:0 0 2 * * *}")
    public void cleanupOldLogs() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
        long deleted = auditLogService.deleteOlderThan(cutoff);
        if (deleted > 0) {
            AppLogManager.info(AuditLogCleanupJob.class,
                    String.format("Deleted %d audit log records older than %s", deleted, cutoff));
        }
    }
}
