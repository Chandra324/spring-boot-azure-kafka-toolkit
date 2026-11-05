package com.chandra.toolkit.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import com.chandra.toolkit.health.KafkaClusterHealthIndicator;

class AzureKafkaAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    AzureKafkaAutoConfiguration.class,
                    AzureKafkaHealthAutoConfiguration.class
            ))
            .withUserConfiguration(KafkaInfrastructureConfiguration.class);

    @Test
    void registersErrorHandlerAndHealthIndicatorByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(CommonErrorHandler.class);
            assertThat(context).hasSingleBean(KafkaClusterHealthIndicator.class);
            assertThat(context).hasSingleBean(AzureKafkaProperties.class);
        });
    }

    @Test
    void canBeDisabled() {
        contextRunner
                .withPropertyValues("azure.kafka.toolkit.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(CommonErrorHandler.class);
                    assertThat(context).doesNotHaveBean(KafkaClusterHealthIndicator.class);
                });
    }

    @Test
    void backsOffWhenCustomErrorHandlerExists() {
        contextRunner
                .withUserConfiguration(CustomErrorHandlerConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(CommonErrorHandler.class);
                    assertThat(context.getBean(CommonErrorHandler.class))
                            .isSameAs(context.getBean("customErrorHandler"));
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class KafkaInfrastructureConfiguration {

        @Bean
        @SuppressWarnings("unchecked")
        KafkaTemplate<Object, Object> kafkaTemplate() {
            return mock(KafkaTemplate.class);
        }

        @Bean
        KafkaAdmin kafkaAdmin() {
            Map<String, Object> configs = new HashMap<>();
            configs.put("bootstrap.servers", "localhost:9092");
            return new KafkaAdmin(configs);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomErrorHandlerConfiguration {

        @Bean
        CommonErrorHandler customErrorHandler() {
            return new DefaultErrorHandler(new FixedBackOff(0L, 0L));
        }
    }
}
