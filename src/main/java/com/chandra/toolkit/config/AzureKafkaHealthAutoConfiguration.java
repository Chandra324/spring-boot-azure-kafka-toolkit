package com.chandra.toolkit.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaAdmin;

import com.chandra.toolkit.health.KafkaClusterHealthIndicator;

/**
 * Optional Actuator health auto-configuration.
 *
 * <p>Only activates when Actuator is present on the classpath.
 */
@AutoConfiguration(after = AzureKafkaAutoConfiguration.class)
@ConditionalOnClass(name = "org.springframework.boot.actuate.health.HealthIndicator")
@ConditionalOnProperty(prefix = "azure.kafka.toolkit", name = "enabled", matchIfMissing = true)
public class AzureKafkaHealthAutoConfiguration {

    /**
     * Registers a Kafka / Event Hub cluster health indicator.
     *
     * @param kafkaAdmin Spring Kafka admin bean
     * @param properties toolkit configuration
     * @return health indicator bean
     */
    @Bean
    @ConditionalOnBean(KafkaAdmin.class)
    @ConditionalOnMissingBean(name = "kafkaClusterHealthIndicator")
    public KafkaClusterHealthIndicator kafkaClusterHealthIndicator(KafkaAdmin kafkaAdmin,
                                                                   AzureKafkaProperties properties) {
        return new KafkaClusterHealthIndicator(kafkaAdmin, properties.getHealthTimeoutMs());
    }
}
