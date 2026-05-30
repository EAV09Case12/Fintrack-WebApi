package com.example.fintrack_webapi.infrastructure.cache;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.Mockito.mock;

class RedisReporteRepositoryTest {

    @Test
    void obtieneReporteExistente() {
        @SuppressWarnings("unchecked")
        ValueOperations<String, byte[]> valueOps = mock(ValueOperations.class);
        RedisTemplate<String, byte[]> redisTemplate = mock(RedisTemplate.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        byte[] expected = new byte[] { 1, 2, 3 };
        when(valueOps.get("reportes:req-1")).thenReturn(expected);

        RedisReporteRepository repo = new RedisReporteRepository(redisTemplate);

        byte[] actual = repo.obtenerReporte("req-1");

        assertArrayEquals(expected, actual);
        verify(valueOps).get("reportes:req-1");
    }

    @Test
    void obtieneReporteInexistente() {
        @SuppressWarnings("unchecked")
        ValueOperations<String, byte[]> valueOps = mock(ValueOperations.class);
        RedisTemplate<String, byte[]> redisTemplate = mock(RedisTemplate.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("reportes:req-2")).thenReturn(null);

        RedisReporteRepository repo = new RedisReporteRepository(redisTemplate);

        assertNull(repo.obtenerReporte("req-2"));
    }
}