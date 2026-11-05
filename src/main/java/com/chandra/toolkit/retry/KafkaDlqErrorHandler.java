package com.chandra.toolkit.retry;

import java.util.Objects;

import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.Assert;
import org.springframework.util.backoff.FixedBackOff;

import com.chandra.toolkit.config.AzureKafkaProperties;

/**
 * Factory for a {@link CommonErrorHandler} that retries failed records and then
 * publishes them to a dead-letter topic derived from the original topic name.
 */
public final class KafkaDlqErrorHandler {

    private KafkaDlqErrorHandler() {
    }

    /**
     * Creates a retrying DLQ error handler using the given Kafka operations and properties.
     *
     * @param template Kafka operations used to publish dead-letter records
     * @param properties toolkit properties controlling retries and DLQ suffix
     * @return a configured {@link CommonErrorHandler}
     */
    public static CommonErrorHandler createHandler(KafkaOperations<Object, Object> template,
                                                   AzureKafkaProperties properties) {
        Objects.requireNonNull(template, "template must not be null");
        Objects.requireNonNull(properties, "properties must not be null");
        Assert.hasText(properties.getDlqTopicSuffix(), "dlqTopicSuffix must not be blank");

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                template,
                (record, exception) -> new TopicPartition(
                        record.topic() + properties.getDlqTopicSuffix(),
                        record.partition()
                )
        );

        FixedBackOff backOff = new FixedBackOff(
                properties.getRetryBackoffMs(),
                properties.getMaxRetries()
        );

        return new DefaultErrorHandler(recoverer, backOff);
    }
}
