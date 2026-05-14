package com.automotiva.estetica.rick.infrastructure.config;

import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Configuração global de cache com Redis.
 *
 * <p>
 * Habilita o mecanismo de caching via anotações Spring
 * (@Cacheable, @CacheEvict). Utiliza serialização JSON padrão para valores e
 * String para chaves, com TTL (time-to-live) de 10 minutos (600 segundos).
 *
 * <p>
 * Referência: https://spring.io/guides/gs/caching-gemfire/
 * https://docs.spring.io/spring-data/redis/docs/current/reference/html/#redis:connectors
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(prefix = "spring.cache", name = "type", havingValue = "redis", matchIfMissing = false)
public class CacheConfig {

    /**
     * Configura o gerenciador de cache Redis com políticas de expiração.
     *
     * @param connectionFactory
     *            factory de conexão Redis
     * @return CacheManager configurado com TTL de 10 minutos
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Configuração padrão: TTL de 10 minutos
        final var defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10));

        return RedisCacheManager.builder(connectionFactory).cacheDefaults(defaultCacheConfig).build();
    }
}
