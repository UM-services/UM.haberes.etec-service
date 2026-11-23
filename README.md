# UM.haberes.etec-service

![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.2-6DB33F?logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.4-6DB33F?logo=spring&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-3-C71A36?logo=apachemaven&logoColor=white)
![OpenFeign](https://img.shields.io/badge/OpenFeign-Spring%20Cloud-grey)
![Consul](https://img.shields.io/badge/Consul-Discovery-F24C53?logo=consul&logoColor=white)
![Caffeine](https://img.shields.io/badge/Caffeine-Cache-grey)
![JUnit](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Image-2496ED?logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-CI%2FCD-2088FF?logo=githubactions&logoColor=white)

Microservicio Spring Boot del sistema de haberes (ETEC). No accede a base de datos:
las validaciones de negocio viven acá y la persistencia —incluido el cálculo de la
liquidación— la resuelve `haberes-core-service` a través de OpenFeign.

Versión actual: `0.4.0` (ver [CHANGELOG.md](CHANGELOG.md)).

## Stack

- Java 25 / Spring Boot 4.1.2 / Spring Cloud 2025.1.4
- Consul (service discovery), OpenFeign (HC5), Caffeine (caché)
- Springdoc OpenAPI, Spring Boot Actuator
- Lombok, JUnit 5 + Mockito

## Funcionalidad

- **Cargos ETEC**: catálogo de cargos con puntos sobre categorías de haberes-core
  (`puntos > 0`, `docente = 1`). El alta toma el próximo `categoriaId` desde `9000`
  y deja `basico = 0`, porque el importe se calcula como `puntos × valor del punto +
  monto fijo`.
- **Asignación de cargos**: vínculo legajo–cargo con fecha de alta/baja. Valida que el
  cargo sea ETEC, que las fechas sean coherentes y que no haya solape con otra
  asignación vigente del mismo cargo.
- **Liquidación de cargos por puntos**: `preview` y generación de `cargo_liquidacion`
  por período o por legajo delegan en `haberes-core-service`, que aplica
  `categoriaBasico = puntos × valor del punto + monto fijo` con los parámetros de
  `control_etec` y el reemplazo idempotente del legajo/período. Este servicio valida
  contra `GET /api/haberes/core/etec/control/{anho}/{mes}/opcional` y conserva el
  contrato (`400` si no hay cargos con puntos, control ETEC o valor del punto).
- **Presentismo**: pérdida de presentismo por asignación de cargo (`/presentismo`)
  con consultas por legajo y período, por período y por asignación, más alta,
  edición del motivo y baja. El CRUD y las validaciones viven en
  `haberes-core-service`; este servicio expone el circuito ETEC y mapea los
  requests propios a los DTO de core.
- **Presentismo por horas** (`/presentismo-horas`): estado del legajo en el período
  (`tieneCargosHoras`), listado por período y CRUD con motivo, delegados en core de
  la misma forma que el presentismo por asignación.
- **Control ETEC del período**: `GET /control/{anho}/{mes}/opcional` expone el
  control que persiste core, con `200` sin cuerpo cuando el período todavía no está
  configurado.
- **Auditoría**: las llamadas Feign propagan `X-Usuario-Legajo` y
  `X-Legajo-Solicitante` para que core registre el actor en las escrituras.
- **Errores**: `ProblemDetail` con `400` de validación, `409` de conflicto y el status
  devuelto por core (`FeignException`); si core responde un cuerpo JSON, se usa
  `detail`, `message` o `error` como detalle.

## Endpoints

Todas las rutas cuelgan de `/api/haberes/etec`.

### Cargo (`/cargo`)

| Método | Ruta | Descripción |
| --- | --- | --- |
| GET | `/cargo/` | Lista los cargos ETEC con puntos |
| GET | `/cargo/{anho}/{mes}` | Lista los cargos ETEC con los puntos vigentes en el período |
| GET | `/cargo/{categoriaId}` | Obtiene un cargo y valida que sea ETEC |
| POST | `/cargo/{anho}/{mes}` | Crea un cargo |
| PUT | `/cargo/{categoriaId}/{anho}/{mes}` | Actualiza nombre y puntos del cargo |
| DELETE | `/cargo/{categoriaId}` | Borra el cargo si no tiene asignaciones |

### Asignación de cargo (`/asignacion-cargo`)

| Método | Ruta | Descripción |
| --- | --- | --- |
| GET | `/asignacion-cargo/legajo/{legajoId}` | Asignaciones del legajo |
| GET | `/asignacion-cargo/periodo/{anho}/{mes}` | Asignaciones vigentes del período |
| GET | `/asignacion-cargo/legajo/{legajoId}/periodo/{anho}/{mes}` | Vigentes del legajo en el período |
| GET | `/asignacion-cargo/{asignacionCargoId}` | Obtiene una asignación |
| POST | `/asignacion-cargo/` | Crea la asignación |
| PUT | `/asignacion-cargo/{asignacionCargoId}` | Actualiza la asignación |
| DELETE | `/asignacion-cargo/{asignacionCargoId}` | Borra la asignación |

### Liquidación de cargos (`/cargo-liquidacion`)

| Método | Ruta | Descripción |
| --- | --- | --- |
| POST | `/cargo-liquidacion/generar/{anho}/{mes}` | Genera la liquidación del período |
| POST | `/cargo-liquidacion/generar/legajo/{legajoId}/{anho}/{mes}` | Genera la liquidación del legajo |
| GET | `/cargo-liquidacion/preview/{anho}/{mes}` | Previsualiza el período sin persistir |
| GET | `/cargo-liquidacion/preview/legajo/{legajoId}/{anho}/{mes}` | Previsualiza el legajo sin persistir |
| GET | `/cargo-liquidacion/legajo/{legajoId}/{anho}/{mes}` | Liquidación registrada del legajo |
| GET | `/cargo-liquidacion/periodo/{anho}/{mes}` | Liquidación registrada del período |

### Presentismo (`/presentismo`)

| Método | Ruta | Descripción |
| --- | --- | --- |
| GET | `/presentismo/legajo/{legajoId}/periodo/{anho}/{mes}` | Presentismos del legajo en el período |
| GET | `/presentismo/periodo/{anho}/{mes}` | Presentismos del período |
| GET | `/presentismo/asignacion/{asignacionCargoId}/periodo/{anho}/{mes}` | Presentismo de la asignación en el período |
| GET | `/presentismo/{presentismoId}` | Obtiene un presentismo |
| POST | `/presentismo/` | Registra la pérdida de presentismo |
| PUT | `/presentismo/{presentismoId}` | Actualiza el motivo |
| DELETE | `/presentismo/{presentismoId}` | Elimina el registro (`204`) |

### Presentismo por horas (`/presentismo-horas`)

| Método | Ruta | Descripción |
| --- | --- | --- |
| GET | `/presentismo-horas/legajo/{legajoId}/periodo/{anho}/{mes}` | Estado del legajo en el período (`tieneCargosHoras`) |
| GET | `/presentismo-horas/periodo/{anho}/{mes}` | Presentismos por horas del período |
| GET | `/presentismo-horas/{presentismoHorasId}` | Obtiene un presentismo por horas |
| POST | `/presentismo-horas/` | Registra la pérdida de presentismo por horas |
| PUT | `/presentismo-horas/{presentismoHorasId}` | Actualiza el motivo |
| DELETE | `/presentismo-horas/{presentismoHorasId}` | Elimina el registro (`204`) |

### Control ETEC (`/control`)

| Método | Ruta | Descripción |
| --- | --- | --- |
| GET | `/control/{anho}/{mes}/opcional` | Control ETEC del período; `200` sin cuerpo si no está configurado |

## Requisitos

- JDK 25
- Docker (opcional)

## Ejecución

```bash
./mvnw spring-boot:run
```

Puerto por defecto: `8094` (configurable con `APP_PORT`). El servicio se registra en
Consul (`consul-service:8500`) y resuelve `haberes-core-service` por service discovery.

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

## Documentación

- [CHANGELOG.md](CHANGELOG.md): historial de versiones.
- `docs/diagrams/`: diagramas Mermaid de arquitectura y despliegue. El workflow
  `.github/workflows/generate-docs.yml` los publica, junto con el árbol de dependencias,
  en GitHub Pages y enlaza la wiki del repositorio.
