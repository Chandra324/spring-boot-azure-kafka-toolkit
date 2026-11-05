package com.chandra.toolkit.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

class AzureKafkaPropertiesTest {

    @Test
    void bindsCustomValues() {
        MapConfigurationPropertySource source = new MapConfigurationPropertySource(Map.of(
                "azure.kafka.toolkit.enabled", "true",
                "azure.kafka.toolkit.max-retries", "5",
                "azure.kafka.toolkit.retry-backoff-ms", "2500",
                "azure.kafka.toolkit.dlq-topic-suffix", ".DEAD",
                "azure.kafka.toolkit.health-timeout-ms", "5000"
        ));

        AzureKafkaProperties properties = new Binder(source)
                .bind("azure.kafka.toolkit", Bindable.of(AzureKafkaProperties.class))
                .get();

        assertThat(properties.isEnabled()).isTrue();
        assertThat(properties.getMaxRetries()).isEqualTo(5);
        assertThat(properties.getRetryBackoffMs()).isEqualTo(2500L);
        assertThat(properties.getDlqTopicSuffix()).isEqualTo(".DEAD");
        assertThat(properties.getHealthTimeoutMs()).isEqualTo(5000L);
    }

    @Test
    void usesDocumentedDefaults() {
        AzureKafkaProperties properties = new AzureKafkaProperties();

        assertThat(properties.isEnabled()).isTrue();
        assertThat(properties.getMaxRetries()).isEqualTo(3);
        assertThat(properties.getRetryBackoffMs()).isEqualTo(1000L);
        assertThat(properties.getDlqTopicSuffix()).isEqualTo(".DLQ");
        assertThat(properties.getHealthTimeoutMs()).isEqualTo(3000L);
    }
}
