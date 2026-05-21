package com.example.fintrack_webapi.infrastructure.config;

import org.springframework.amqp.core.*;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String REPORT_EXCHANGE =
            "fintrack.report.exchange";

    public static final String REPORT_QUEUE =
            "fintrack.report.monthly.queue";

    public static final String REPORT_ROUTING_KEY =
            "report.monthly.generate";

    public static final String REPORT_DLQ =
            "fintrack.report.monthly.dlq";

    @Bean
    public TopicExchange reportExchange() {

        return new TopicExchange(
                REPORT_EXCHANGE
        );
    }

    @Bean
    public Queue reportQueue() {

        return QueueBuilder
                .durable(REPORT_QUEUE)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {

        return QueueBuilder
                .durable(REPORT_DLQ)
                .build();
    }

    @Bean
    public Binding reportBinding(
            Queue reportQueue,
            TopicExchange reportExchange
    ) {

        return BindingBuilder
                .bind(reportQueue)
                .to(reportExchange)
                .with(REPORT_ROUTING_KEY);
    }

    @Bean
    public MessageConverter
    jsonMessageConverter() {

        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {

        RabbitTemplate template =
                new RabbitTemplate(
                        connectionFactory
                );

        template.setMessageConverter(
                messageConverter
        );

        return template;
    }
}