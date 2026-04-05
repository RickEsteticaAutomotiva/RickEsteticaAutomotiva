package com.automotiva.estetica.rick.infrastructure.config;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;

@ExtendWith(MockitoExtension.class)
class FlywayConfigTest {

    @Mock
    private FluentConfiguration fluentConfiguration;

    @Test
    void flywayConfigurationCustomizer_deveAplicarConfiguracoesPadrao() {
        FlywayConfig flywayConfig = new FlywayConfig();
        FlywayConfigurationCustomizer customizer = flywayConfig.flywayConfigurationCustomizer();

        when(fluentConfiguration.encoding("UTF-8")).thenReturn(fluentConfiguration);
        when(fluentConfiguration.outOfOrder(false)).thenReturn(fluentConfiguration);
        when(fluentConfiguration.validateOnMigrate(true)).thenReturn(fluentConfiguration);
        when(fluentConfiguration.cleanDisabled(true)).thenReturn(fluentConfiguration);
        when(fluentConfiguration.loggers("slf4j")).thenReturn(fluentConfiguration);

        customizer.customize(fluentConfiguration);

        verify(fluentConfiguration).encoding("UTF-8");
        verify(fluentConfiguration).outOfOrder(false);
        verify(fluentConfiguration).validateOnMigrate(true);
        verify(fluentConfiguration).cleanDisabled(true);
        verify(fluentConfiguration).loggers("slf4j");
    }
}

