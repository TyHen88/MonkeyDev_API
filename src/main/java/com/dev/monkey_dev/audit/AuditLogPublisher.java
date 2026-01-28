package com.dev.monkey_dev.audit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dev.monkey_dev.logging.AppLogManager;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditLogPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.audit.exchange}")
    private String auditExchange;

    @Value("${app.rabbitmq.audit.routing-key}")
    private String auditRoutingKey;

    public void publish(AuditLogEvent event) {
        try {
            rabbitTemplate.convertAndSend(auditExchange, auditRoutingKey, event);
        } catch (Exception ex) {
            AppLogManager.warn(AuditLogPublisher.class.getName(), "Failed to publish audit log event", ex);
        }
    }
}
