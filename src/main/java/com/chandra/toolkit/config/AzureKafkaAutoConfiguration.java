package com.chandra.toolkit.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;

import com.chandra.toolkit.retry.KafkaDlqErrorHandler;

/**
 * Auto-configuration for toolkit DLQ error handling.
 *
 * <p>Applied after Spring Boot's {@link KafkaAutoConfiguration} so that
 * {@link KafkaTemplate} beans are available.
 */
@AutoConfiguration(after = KafkaAutoConfiguration.class)
@EnableConfigurationProperties(AzureKafkaProperties.class)
@ConditionalOnClass(KafkaTemplate.class)
@ConditionalOnProperty(prefix = "azure.kafka.toolkit", name = "enabled", matchIfMissing = true)
public class AzureKafkaAutoConfiguration {

    /**
     * Registers a {@link CommonErrorHandler} that retries and routes poison messages to a DLQ topic.
     *
     * @param kafkaTemplate Kafka template used for dead-letter publishing
     * @param properties toolkit configuration
     * @return shared error handler bean
     */
    @Bean
    @ConditionalOnBean(KafkaTemplate.class)
    @ConditionalOnMissingBean(CommonErrorHandler.class)
    public CommonErrorHandler kafkaErrorHandler(KafkaTemplate<Object, Object> kafkaTemplate,
                                                AzureKafkaProperties properties) {
        return KafkaDlqErrorHandler.createHandler(kafkaTemplate, properties);
    }
}
