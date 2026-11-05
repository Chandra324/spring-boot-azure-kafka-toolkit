package com.chandra.toolkit.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.kafka.core.KafkaAdmin;

class KafkaClusterHealthIndicatorTest {

    @Test
    void reportsDownWhenConfigurationEmpty() {
        KafkaAdmin kafkaAdmin = mock(KafkaAdmin.class);
        when(kafkaAdmin.getConfigurationProperties()).thenReturn(Map.of());

        KafkaClusterHealthIndicator indicator = new KafkaClusterHealthIndicator(kafkaAdmin, 1000L);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("error", "KafkaAdmin configuration is empty");
    }

    @Test
    void reportsUpWhenClusterDescribeSucceeds() {
        KafkaAdmin kafkaAdmin = mock(KafkaAdmin.class);
        when(kafkaAdmin.getConfigurationProperties()).thenReturn(Map.of("bootstrap.servers", "localhost:9092"));

        AdminClient adminClient = mock(AdminClient.class);
        DescribeClusterResult describeClusterResult = mock(DescribeClusterResult.class);
        Node controller = new Node(1, "localhost", 9092);

        when(adminClient.describeCluster()).thenReturn(describeClusterResult);
        when(describeClusterResult.clusterId()).thenReturn(completedFuture("cluster-1"));
        when(describeClusterResult.nodes()).thenReturn(completedFuture(List.of(controller)));
        when(describeClusterResult.controller()).thenReturn(completedFuture(controller));

        KafkaClusterHealthIndicator indicator =
                new KafkaClusterHealthIndicator(kafkaAdmin, 1000L, configs -> adminClient);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails())
                .containsEntry("clusterId", "cluster-1")
                .containsEntry("nodes", 1)
                .containsEntry("controller", "1");
    }

    @Test
    void reportsDownWhenProbeFails() {
        KafkaAdmin kafkaAdmin = mock(KafkaAdmin.class);
        when(kafkaAdmin.getConfigurationProperties()).thenReturn(Map.of("bootstrap.servers", "localhost:9092"));

        KafkaClusterHealthIndicator indicator = new KafkaClusterHealthIndicator(
                kafkaAdmin,
                1000L,
                configs -> {
                    throw new IllegalStateException("broker unreachable");
                }
        );

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().get("error").toString()).contains("Failed to connect");
    }

    @Test
    void rejectsInvalidTimeout() {
        KafkaAdmin kafkaAdmin = mock(KafkaAdmin.class);

        assertThatThrownBy(() -> new KafkaClusterHealthIndicator(kafkaAdmin, 50L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("healthTimeoutMs");
    }

    private static <T> KafkaFuture<T> completedFuture(T value) {
        return KafkaFuture.completedFuture(value);
    }
}
