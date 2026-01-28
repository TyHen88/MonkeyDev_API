package com.dev.monkey_dev.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String AUDIT_EXCHANGE_BEAN = "auditExchange";

    @Value("${app.rabbitmq.audit.exchange}")
    private String auditExchange;

    @Value("${app.rabbitmq.audit.queue}")
    private String auditQueue;

    @Value("${app.rabbitmq.audit.routing-key}")
    private String auditRoutingKey;

    @Bean
    public Queue auditQueue() {
        return new Queue(auditQueue, true);
    }

    @Bean(name = AUDIT_EXCHANGE_BEAN)
    public TopicExchange auditExchange() {
        return new TopicExchange(auditExchange);
    }

    @Bean
    public Binding auditBinding(Queue auditQueue, TopicExchange auditExchange) {
        return BindingBuilder.bind(auditQueue).to(auditExchange).with(auditRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
