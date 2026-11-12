# AGENTS.md

Guía para agentes que trabajen en este repositorio. Aplica a todo el proyecto.

## Qué es este servicio

Microservicio Spring Boot del sistema de haberes para el tenant **ETEC**
(`haberes-etec-service`, puerto `8094`). **No accede a base de datos y no tiene JPA**:
las reglas de negocio viven acá y la persistencia la ejecuta `haberes-core-service`
a través de OpenFeign. No introduzcas entidades, repositorios ni migraciones.

Base de los endpoints propios: `/api/haberes/etec/**`.
Base de los endpoints de core: `/api/haberes/core/**`.

## Stack

- Java 25, Spring Boot 4.1.2, Spring Cloud 2025.1.4, Maven (`./mvnw`).
- Consul para discovery, OpenFeign con HC5, Caffeine para caché.
- Lombok, Jakarta Bean Validation, Springdoc, Actuator.
- JUnit 5, Mockito y AssertJ.

## Comandos

```bash
./mvnw test          # tests
./mvnw verify        # lo que corre CI (incluye JaCoCo)
./mvnw spring-boot:run
docker build -t um.haberes.etec-service .
```

Health: `/actuator/health`; readiness: `/actuator/health/readiness`;
Swagger UI: `/swagger-ui.html`.

## Estructura

Slices por contexto, cada uno con `application/` (servicios) e
`infrastructure/web/` (controllers y `dto/`):

```text
src/main/java/um/haberes/etec/
├── Application.java
├── cargo/                  # catálogo de cargos ETEC (categorías con puntos)
├── asignacion_cargo/       # vínculo legajo–cargo
├── generacion/             # preview y generación de cargo_liquidacion
├── client/core/            # @FeignClient hacia haberes-core + dto/
└── shared/
    ├── audit/              # propagación del actor de auditoría
    ├── exception/          # EtecValidationException, EtecConflictException
    ├── util/               # PeriodoUtils
    └── web/                # EtecExceptionHandler
```

- Los `@FeignClient` deben vivir en `um.haberes.etec.client`:
  `@EnableFeignClients(basePackages = "um.haberes.etec.client")` en `Application`.
- Cada cliente Feign usa `name = "haberes-core-service"` y un `contextId` único.
- Los DTO de core viven en `client/core/dto` y reflejan el contrato de core; los
  request del API propio van en `infrastructure/web/dto`. No mezcles unos con otros.

## Reglas de negocio que no se deben romper

- **Cargo ETEC**: es una categoría de core con `puntos > 0` y `docente = 1`. El alta
  toma el próximo `categoriaId` (desde `9000` si no hay categorías) y persiste
  `basico = 0`, `noDocente = 0`, `liquidaPorHora = 0`, porque el importe se calcula
  como `puntos × valor del punto`. La baja se rechaza si la categoría ya tiene
  asignaciones.
- **Asignación**: valida que el cargo sea ETEC, que `fechaBaja` no sea anterior a
  `fechaAlta` y que no haya solape con otra asignación del mismo legajo y categoría
  (el propio registro se excluye al editar).
- **Liquidación**: `categoriaBasico = puntos del cargo × valorPuntoEtec` del período,
  con `BigDecimal`, escala 2 y `RoundingMode.HALF_UP`. Nunca uses `double`.
  Fechas del período con `PeriodoUtils.firstDay/lastDay` (UTC).
- La generación **reemplaza** el período o el legajo completo en core para ser
  idempotente. `preview` no debe escribir en core.
- Falla con `400` si el período no tiene `valorPuntoEtec > 0` o si no hay cargos ETEC
  con puntos configurados.

## Convenciones de código

- Inyección por constructor con `@RequiredArgsConstructor` y campos `private final`.
- DTOs con Lombok (`@Getter/@Setter/@Builder/@NoArgsConstructor/@AllArgsConstructor`)
  y `@JsonIgnoreProperties(ignoreUnknown = true)` en los DTO de respuesta de core.
- `OffsetDateTime` en UTC con el patrón `yyyy-MM-dd'T'HH:mm:ssXX` vía `@JsonFormat`.
- Requests propios: `@Valid @RequestBody`; validaciones con Jakarta Bean Validation.
- **Nunca agregues `@RequestParam`** en endpoints nuevos: usá `@PathVariable` para
  ids/filtros simples y `@RequestBody` (con `POST` si hace falta) para filtros
  compuestos. Los existentes se dejan como están.
- Errores: lanzá `EtecValidationException` (`400`) o `EtecConflictException` (`409`);
  `EtecExceptionHandler` arma el `ProblemDetail`. No devuelvas errores ad-hoc ni
  envuelvas `FeignException` para ocultar el status de core.
- No agregues interceptores de auditoría por cliente: `FeignAuditActorConfig` ya
  propaga `X-Usuario-Legajo` y `X-Legajo-Solicitante` a todas las llamadas Feign.
- Nombres, mensajes, logs y tests en español, como el resto del repositorio.
- Mantené el estilo existente: 4 espacios, imports ordenados (java, terceros,
  proyecto), líneas de hasta ~120 caracteres.

## Configuración

- La configuración del servicio está en `src/main/resources/bootstrap.yml`
  (`spring-cloud-starter-bootstrap`). No la muevas a `application.yml`.
- El contrato de readiness con Consul está congelado por
  `ConsulReadinessConfigTest`: si tocás `bootstrap.yml`, ese test debe seguir pasando.

## Tests

- Unitarios de servicios: `@ExtendWith(MockitoExtension.class)`, `@Mock` para los
  clientes Feign, `@InjectMocks` para el servicio. Sin contexto de Spring salvo
  `ApplicationTests`.
- Nombres de test en presente/imperativo en español, describiendo la regla
  (`rechazaBajaAnteriorAlAlta`, `permiteEditarLaMismaAsignacionSinConsiderarlaSolape`).
- Al agregar o cambiar reglas de negocio, agregá o ajustá el test en el mismo slice
  (`src/test/java/um/haberes/etec/<slice>/`).
- Usá AssertJ (`assertThat`) o las aserciones de JUnit 5 ya presentes.

## Documentación

- `README.md`: funcionalidad, endpoints y stack. Actualizalo si cambian endpoints,
  versiones o el modelo general.
- `CHANGELOG.md`: Keep a Changelog + SemVer. Registrá los cambios relevantes en
  español.
- Diagramas Mermaid en `docs/diagrams/`. Si agregás uno, sumalo al manifiesto
  `diagramsManifest` de `.github/workflows/generate-docs.yml`, que publica en GitHub
  Pages y wiki. Validá la sintaxis Mermaid antes de commitear.

## Git y CI

- Commits en **Conventional Commits**, en español, asunto en imperativo de 50
  caracteres o menos y un solo cambio lógico por commit.
- No ejecutes `git commit`, `git push`, creación de ramas/worktrees ni operaciones
  destructivas sin autorización explícita del usuario.
- CI: `maven.yml` (main: `mvn -B verify` + SonarCloud + imagen), `deploy-develop.yml`,
  `deploy-staging.yml` y `generate-docs.yml`. Antes de dar por terminado un cambio,
  corré `./mvnw verify`.
- Los hooks de git reconstruyen el grafo de `graphify-out/` en segundo plano; que
  aparezca sucio es esperado.

## Grafo de conocimiento

Si existe `graphify-out/graph.json`, consultá el grafo antes de grepear el código:

```bash
graphify query "<pregunta>"   # contexto acotado
graphify path "<A>" "<B>"     # relación entre conceptos
graphify explain "<nodo>"     # nodo y vecinos
graphify affected "<X>"       # impacto inverso
```

Leé `graphify-out/GRAPH_REPORT.md` solo para revisión de arquitectura amplia.
