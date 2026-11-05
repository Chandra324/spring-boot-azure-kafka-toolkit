package com.chandra.toolkit.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for the Azure Kafka toolkit starter.
 *
 * <p>Bound from {@code azure.kafka.toolkit.*} in application configuration.
 */
@Validated
@ConfigurationProperties(prefix = "azure.kafka.toolkit")
public class AzureKafkaProperties {

    /**
     * Whether toolkit auto-configuration is active.
     */
    private boolean enabled = true;

    /**
     * Maximum number of consumption retries before DLQ routing.
     */
    @Min(0)
    private int maxRetries = 3;

    /**
     * Delay between retry attempts, in milliseconds.
     */
    @Min(0)
    private long retryBackoffMs = 1000L;

    /**
     * Suffix appended to the original topic when publishing to the DLQ.
     */
    @NotBlank
    private String dlqTopicSuffix = ".DLQ";

    /**
     * Timeout used by the Kafka cluster health probe, in milliseconds.
     */
    @Min(100)
    private long healthTimeoutMs = 3000L;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public long getRetryBackoffMs() {
        return retryBackoffMs;
    }

    public void setRetryBackoffMs(long retryBackoffMs) {
        this.retryBackoffMs = retryBackoffMs;
    }

    public String getDlqTopicSuffix() {
        return dlqTopicSuffix;
    }

    public void setDlqTopicSuffix(String dlqTopicSuffix) {
        this.dlqTopicSuffix = dlqTopicSuffix;
    }

    public long getHealthTimeoutMs() {
        return healthTimeoutMs;
    }

    public void setHealthTimeoutMs(long healthTimeoutMs) {
        this.healthTimeoutMs = healthTimeoutMs;
    }
}
