package com.example.fintrack_webapi.infrastructure.cache;

import org.springframework.data.redis.core
        .RedisTemplate;

import org.springframework.stereotype.Repository;

import com.example.fintrack_webapi.domain.port.output
        .ReporteCachePort;

@Repository
public class RedisReporteRepository
        implements ReporteCachePort {

    private static final String PREFIX =
            "reportes:";

    private final RedisTemplate<String, byte[]>
            redisTemplate;

    public RedisReporteRepository(
            RedisTemplate<String, byte[]>
                    redisTemplate
    ) {

        this.redisTemplate =
                redisTemplate;
    }

    @Override
    public byte[] obtenerReporte(
            String requestId
    ) {

        return redisTemplate
                .opsForValue()
                .get(
                        generarKey(requestId)
                );
    }

    private String generarKey(
            String requestId
    ) {

        return PREFIX + requestId;
    }
}
