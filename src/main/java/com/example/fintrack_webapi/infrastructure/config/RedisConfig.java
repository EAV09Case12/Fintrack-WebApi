package com.example.fintrack_webapi.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.connection.RedisConnectionFactory;

import org.springframework.data.redis.connection.lettuce
        .LettuceConnectionFactory;

import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.serializer
        .RedisSerializer;

import org.springframework.data.redis.serializer
        .StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory
    redisConnectionFactory() {

        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, byte[]>
    redisTemplate(
            RedisConnectionFactory
                    connectionFactory
    ) {

        RedisTemplate<String, byte[]>
                template =
                new RedisTemplate<>();

        template.setConnectionFactory(
                connectionFactory
        );

        template.setKeySerializer(
                new StringRedisSerializer()
        );

        template.setValueSerializer(
                RedisSerializer.byteArray()
        );

        template.afterPropertiesSet();

        return template;
    }
}