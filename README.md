# Spring Boot Azure Kafka & Event Hub Toolkit

Spring Boot 3 starter for Apache Kafka and Azure Event Hub. It provides retryable
consumer error handling with dead-letter routing, validated configuration properties,
and an optional Actuator health probe.

## Features

- Auto-configuration after Spring Kafka setup (`AutoConfiguration.imports`)
- Configurable retry backoff with DLQ topic routing (`DefaultErrorHandler` + `DeadLetterPublishingRecoverer`)
- Optional Actuator health check via `AdminClient.describeCluster()`
- Property validation and IDE configuration metadata
- Sample service showing producer / consumer usage

## Modules

| Module | Description |
|--------|-------------|
| `toolkit-core` | Reusable starter library |
| `toolkit-sample-app` | Demo microservice |

## Requirements

- JDK 17+
- Maven 3.9+
- Kafka broker or Azure Event Hubs Kafka endpoint (for the sample app)

## Quick start

```bash
git clone https://github.com/Chandra324/spring-boot-azure-kafka-toolkit.git
cd spring-boot-azure-kafka-toolkit
mvn clean verify
```

Run the sample application:

```bash
cd toolkit-sample-app
mvn spring-boot:run
```

Publish an event:

```bash
curl -X POST "http://localhost:8080/api/events?topic=enterprise-orders" \
  -H "Content-Type: text/plain" \
  -d "order-1001"
```

Trigger DLQ handling with a failing payload:

```bash
curl -X POST "http://localhost:8080/api/events?topic=enterprise-orders" \
  -H "Content-Type: text/plain" \
  -d "order-fail-demo"
```

Health endpoint:

```bash
curl http://localhost:8080/actuator/health
```

## Using the starter

```xml
<dependency>
    <groupId>com.chandra.toolkit</groupId>
    <artifactId>toolkit-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

Configure your broker with the usual `spring.kafka.*` properties, then tune the toolkit:

```yaml
azure:
  kafka:
    toolkit:
      enabled: true
      max-retries: 3
      retry-backoff-ms: 2000
      dlq-topic-suffix: .DLQ
      health-timeout-ms: 3000
```

### Configuration reference

| Property | Default | Description |
|----------|---------|-------------|
| `azure.kafka.toolkit.enabled` | `true` | Enables toolkit auto-configuration |
| `azure.kafka.toolkit.max-retries` | `3` | Retries before DLQ publish |
| `azure.kafka.toolkit.retry-backoff-ms` | `1000` | Delay between retries (ms) |
| `azure.kafka.toolkit.dlq-topic-suffix` | `.DLQ` | Suffix for DLQ topic names |
| `azure.kafka.toolkit.health-timeout-ms` | `3000` | Health probe timeout (ms) |

### Azure Event Hubs

Point `spring.kafka.bootstrap-servers` at your Event Hubs Kafka endpoint and configure
SASL/JAAS as required by your namespace. The toolkit remains broker-agnostic once the
Kafka client can authenticate and connect.

## Engineering standards

- Java 17, Spring Boot 3.2.x
- Maven Enforcer (JDK / Maven version gates)
- Spotless import and whitespace checks
- Unit tests + `ApplicationContextRunner` auto-config tests
- JaCoCo line coverage gate on `toolkit-core` (70%)
- GitHub Actions CI on JDK 17 and 21

See [CONTRIBUTING.md](CONTRIBUTING.md) and [CHANGELOG.md](CHANGELOG.md).

## License

MIT — see [LICENSE](LICENSE).
