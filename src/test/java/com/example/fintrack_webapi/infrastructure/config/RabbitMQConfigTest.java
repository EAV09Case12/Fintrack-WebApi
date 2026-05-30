package com.example.fintrack_webapi.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import static org.mockito.Mockito.mock;

class RabbitMQConfigTest {

    private final RabbitMQConfig config = new RabbitMQConfig();

    @Test
    void declaraExchangeQueueYBinding() {
        TopicExchange exchange = config.reportExchange();
        Queue queue = config.reportQueue();
        Binding binding = config.reportBinding();

        assertEquals(RabbitMQConfig.REPORT_EXCHANGE, exchange.getName());
        assertEquals(RabbitMQConfig.REPORT_QUEUE, queue.getName());
        assertEquals(RabbitMQConfig.REPORT_ROUTING_KEY, binding.getRoutingKey());
        assertEquals(RabbitMQConfig.REPORT_EXCHANGE, binding.getExchange());
    }

    @Test
    void configuraConverterTemplatedYAdmin() {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);

        RabbitTemplate template = config.rabbitTemplate(connectionFactory);
        RabbitAdmin admin = config.rabbitAdmin(connectionFactory);

        assertInstanceOf(Jackson2JsonMessageConverter.class, template.getMessageConverter());
        assertTrue(admin.isAutoStartup());
    }
}