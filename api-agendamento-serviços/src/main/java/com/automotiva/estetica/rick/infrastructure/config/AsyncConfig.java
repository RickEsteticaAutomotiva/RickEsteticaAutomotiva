package com.automotiva.estetica.rick.infrastructure.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configura o pool de threads dedicado para persistência assíncrona de logs de
 * erro.
 *
 * <p>
 * O uso de um pool separado (erroLogTaskExecutor) garante que a gravação de
 * logs nunca consuma threads do pool principal da aplicação.
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    /**
     * Pool dedicado para o fluxo assíncrono do ErroLogApplicationService.
     *
     * <ul>
     * <li>corePoolSize=2 — threads mínimas sempre ativas
     * <li>maxPoolSize=5 — limite em picos de erros
     * <li>queueCapacity=500 — fila para absorver rajadas sem perder logs
     * </ul>
     */
    @Bean(name = "erroLogTaskExecutor")
    public Executor erroLogTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("erro-log-");
        executor.initialize();
        return executor;
    }
}
