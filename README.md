# UM.haberes.etec-service

Microservicio Spring Boot del sistema de haberes (ETEC).

## Stack

- Java 25 / Spring Boot 4.1.2 / Spring Cloud
- Consul (service discovery), OpenFeign, Caffeine
- Springdoc OpenAPI, Spring Boot Actuator

## Requisitos

- JDK 25
- Docker (opcional)

## Ejecución

```bash
./mvnw spring-boot:run
```

Puerto por defecto: `8094` (configurable con `APP_PORT`).

- Health: `/actuator/health`
- Readiness: `/actuator/health/readiness`
- Swagger UI: `/swagger-ui.html`

## Build

```bash
./mvnw clean package
docker build -t um.haberes.etec-service .
```

## Tests

```bash
./mvnw test
```
