package com.example.fintrack_webapi.infrastructure.messaging;

import com.example.fintrack_webapi.application.dto.events.ReporteMensualEvent;
import com.example.fintrack_webapi.infrastructure.config.RabbitMQConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReporteProducer {

    private static final Logger logger =
            LoggerFactory.getLogger(ReporteProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public ReporteProducer(
            RabbitTemplate rabbitTemplate
    ) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarReporteMensual(
            ReporteMensualEvent event
    ) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.REPORT_EXCHANGE,
                RabbitMQConfig.REPORT_ROUTING_KEY,
                event
        );

        logger.info(
                "Reporte enviado a RabbitMQ. requestId={}",
                event.getRequestId()
        );
    }
}