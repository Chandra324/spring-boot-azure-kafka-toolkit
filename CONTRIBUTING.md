# Contributing

Thanks for considering a contribution to **spring-boot-azure-kafka-toolkit**.

## Development setup

1. Install JDK 17+ and Maven 3.9+.
2. Clone the repository and build:

```bash
mvn clean verify
```

`verify` runs unit tests, Spotless formatting checks, and JaCoCo coverage gates for `toolkit-core`.

## Coding standards

- Target Java 17 APIs; keep Spring Boot 3 auto-configuration patterns.
- Prefer constructor injection and immutable collaborators where practical.
- Document public types and methods with concise Javadoc.
- Keep changes focused; avoid unrelated refactors in the same PR.
- Do not commit secrets, local broker credentials, or generated `target/` output.

## Pull requests

1. Create a branch from `main`.
2. Add or update tests for behavior changes.
3. Run `mvn clean verify` locally.
4. Open a PR with a short summary of the problem and the solution.

## Reporting issues

Please include:

- Spring Boot / Java versions
- Relevant `azure.kafka.toolkit.*` and `spring.kafka.*` settings (redact secrets)
- Expected vs actual behavior and stack traces when available
