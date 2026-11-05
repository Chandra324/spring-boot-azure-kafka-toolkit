package com.chandra.toolkit.health;

import java.util.Map;

import org.apache.kafka.clients.admin.AdminClient;

/**
 * Creates {@link AdminClient} instances from Kafka admin configuration.
 *
 * <p>Extracted for unit testing; production code uses {@link AdminClient#create(Map)}.
 */
@FunctionalInterface
public interface AdminClientFactory {

    /**
     * @param configs Kafka admin client configuration
     * @return a new admin client
     */
    AdminClient create(Map<String, Object> configs);
}
