package com.chandra.toolkit.retry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;

import com.chandra.toolkit.config.AzureKafkaProperties;

class KafkaDlqErrorHandlerTest {

    @Test
    @SuppressWarnings("unchecked")
    void createHandlerReturnsDefaultErrorHandler() {
        KafkaOperations<Object, Object> operations = mock(KafkaOperations.class);
        AzureKafkaProperties properties = new AzureKafkaProperties();

        CommonErrorHandler handler = KafkaDlqErrorHandler.createHandler(operations, properties);

        assertThat(handler).isInstanceOf(DefaultErrorHandler.class);
    }

    @Test
    void createHandlerRejectsNullTemplate() {
        assertThatThrownBy(() -> KafkaDlqErrorHandler.createHandler(null, new AzureKafkaProperties()))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("template");
    }

    @Test
    @SuppressWarnings("unchecked")
    void createHandlerRejectsBlankDlqSuffix() {
        KafkaOperations<Object, Object> operations = mock(KafkaOperations.class);
        AzureKafkaProperties properties = new AzureKafkaProperties();
        properties.setDlqTopicSuffix(" ");

        assertThatThrownBy(() -> KafkaDlqErrorHandler.createHandler(operations, properties))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dlqTopicSuffix");
    }
}
