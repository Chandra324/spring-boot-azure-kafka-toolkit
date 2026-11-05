package com.chandra.toolkit.health;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.Node;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * Actuator {@link HealthIndicator} that probes the Kafka / Event Hub cluster
 * via {@link AdminClient#describeCluster()}.
 */
public class KafkaClusterHealthIndicator implements HealthIndicator {

    private final KafkaAdmin kafkaAdmin;
    private final long healthTimeoutMs;
    private final AdminClientFactory adminClientFactory;

    /**
     * @param kafkaAdmin Spring Kafka admin bean providing broker configuration
     * @param healthTimeoutMs maximum wait time for cluster metadata
     */
    public KafkaClusterHealthIndicator(KafkaAdmin kafkaAdmin, long healthTimeoutMs) {
        this(kafkaAdmin, healthTimeoutMs, AdminClient::create);
    }

    /**
     * Package-visible constructor for tests.
     *
     * @param kafkaAdmin Spring Kafka admin bean
     * @param healthTimeoutMs maximum wait time for cluster metadata
     * @param adminClientFactory factory used to create admin clients
     */
    KafkaClusterHealthIndicator(KafkaAdmin kafkaAdmin,
                                long healthTimeoutMs,
                                AdminClientFactory adminClientFactory) {
        this.kafkaAdmin = Objects.requireNonNull(kafkaAdmin, "kafkaAdmin must not be null");
        this.adminClientFactory = Objects.requireNonNull(adminClientFactory, "adminClientFactory must not be null");
        if (healthTimeoutMs < 100L) {
            throw new IllegalArgumentException("healthTimeoutMs must be >= 100");
        }
        this.healthTimeoutMs = healthTimeoutMs;
    }

    @Override
    public Health health() {
        Map<String, Object> configuration = kafkaAdmin.getConfigurationProperties();
        if (configuration == null || configuration.isEmpty()) {
            return Health.down()
                    .withDetail("error", "KafkaAdmin configuration is empty")
                    .build();
        }

        try (AdminClient client = adminClientFactory.create(configuration)) {
            DescribeClusterResult cluster = client.describeCluster();
            String clusterId = cluster.clusterId().get(healthTimeoutMs, TimeUnit.MILLISECONDS);
            Collection<Node> nodes = cluster.nodes().get(healthTimeoutMs, TimeUnit.MILLISECONDS);
            String controller = null;
            Node controllerNode = cluster.controller().get(healthTimeoutMs, TimeUnit.MILLISECONDS);
            if (controllerNode != null) {
                controller = controllerNode.idString();
            }

            return Health.up()
                    .withDetail("clusterId", clusterId)
                    .withDetail("nodes", nodes == null ? 0 : nodes.size())
                    .withDetail("controller", controller)
                    .build();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return Health.down(ex)
                    .withDetail("error", "Interrupted while probing Kafka cluster")
                    .build();
        } catch (Exception ex) {
            return Health.down(ex)
                    .withDetail("error", "Failed to connect to Kafka / Azure Event Hub cluster")
                    .build();
        }
    }
}
