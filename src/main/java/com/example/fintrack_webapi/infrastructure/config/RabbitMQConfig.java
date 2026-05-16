package com.example.fintrack_webapi.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String REPORT_QUEUE = "report.monthly.queue";

    public static final String REPORT_EXCHANGE = "report.monthly.exchange";

    public static final String REPORT_ROUTING_KEY = "report.monthly.generated";

    @Bean
    public Queue reportQueue() {
        return new Queue(REPORT_QUEUE, true);
    }

    @Bean
    public TopicExchange reportExchange() {
        return new TopicExchange(REPORT_EXCHANGE, true, false);
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
    public MessageConverter jsonMessageConverter() {

        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        template.setMessageConverter(jsonMessageConverter);

        return template;
    }
}
