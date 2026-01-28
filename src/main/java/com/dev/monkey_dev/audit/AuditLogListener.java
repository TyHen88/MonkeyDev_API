package com.dev.monkey_dev.audit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditLogListener {

    private final AuditLogService auditLogService;

    @RabbitListener(queues = "${app.rabbitmq.audit.queue}")
    public void onAuditLog(AuditLogEvent event) {
        auditLogService.save(event);
    }
}
