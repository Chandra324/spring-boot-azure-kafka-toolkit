# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-11-05

### Added

- Spring Boot 3 auto-configuration for Kafka DLQ retry handling
- Configurable `azure.kafka.toolkit.*` properties with validation and IDE metadata
- Optional Actuator health indicator that probes cluster metadata through `AdminClient`
- Sample application demonstrating publish, consume, and failure-to-DLQ flow
- Maven Enforcer, Spotless, JaCoCo, source/javadoc jars, and GitHub Actions CI
