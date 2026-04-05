package com.automotiva.estetica.rick.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.concurrent.Executor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@DisplayName("Testes de AsyncConfig")
class AsyncConfigTest {

    private final AsyncConfig asyncConfig = new AsyncConfig();

    @Test
    @DisplayName("deve criar pool dedicado para logs de erro")
    void erroLogTaskExecutor_deveCriarPoolComConfiguracaoEsperada() {
        Executor executor = asyncConfig.erroLogTaskExecutor();

        ThreadPoolTaskExecutor threadPoolExecutor = assertInstanceOf(ThreadPoolTaskExecutor.class, executor);
        assertEquals(2, threadPoolExecutor.getCorePoolSize());
        assertEquals(5, threadPoolExecutor.getMaxPoolSize());
        assertEquals(500, threadPoolExecutor.getQueueCapacity());
        assertEquals("erro-log-", threadPoolExecutor.getThreadNamePrefix());

        threadPoolExecutor.shutdown();
    }
}

