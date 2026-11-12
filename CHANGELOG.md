# Changelog

Todos los cambios relevantes de este proyecto se documentan en este archivo.
El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/)
y el versionado [Semantic Versioning](https://semver.org/lang/es/).

## [0.1.0] - 2026-11-12

Primera versión funcional del servicio: cargos ETEC con puntos, asignación de
cargos y generación de `cargo_liquidacion`. La persistencia sigue delegada en
`haberes-core-service` vía OpenFeign.

### Agregado

- Cargos ETEC (`/api/haberes/etec/cargo`): catálogo de cargos sobre categorías de
  haberes-core con `puntos > 0` y `docente = 1`. El alta calcula el próximo
  `categoriaId` a partir de `9000` y persiste `basico = 0`, porque el importe surge
  de `puntos × valor del punto`. La baja se rechaza con `409` si el cargo ya tiene
  asignaciones registradas.
- Asignación de cargos (`/api/haberes/etec/asignacion-cargo`): alta, edición, baja
  y consultas por legajo y por período. Se valida que el cargo exista como cargo
  ETEC, que la fecha de baja no sea anterior a la de alta y que no haya solape con
  otra asignación vigente del mismo cargo y legajo.
- Generación de liquidación (`/api/haberes/etec/cargo-liquidacion`): `preview` y
  generación por período o por legajo, con `categoriaBasico = puntos del cargo ×
  valor del punto ETEC` del período (redondeo `HALF_UP` a 2 decimales) y fechas
  desde/hasta del período en UTC. La escritura reemplaza el período/legajo completo
  en core para que la operación sea idempotente. Falla con `400` si el período no
  tiene `valorPuntoEtec > 0` o si no hay cargos ETEC configurados.
- Consulta de `cargo_liquidacion` por legajo y por período, filtrada a las
  categorías ETEC con puntos.
- Clientes Feign hacia `haberes-core-service` para categorías, control de período,
  asignación de cargos y cargo-liquidación, cada uno con su `contextId`.
- `FeignAuditActorConfig`: interceptor que propaga `X-Usuario-Legajo` y
  `X-Legajo-Solicitante` desde la request entrante para que las escrituras
  delegadas en core queden auditadas con actor. No agrega cabeceras en hilos sin
  request.
- `EtecExceptionHandler`: respuestas `ProblemDetail` uniformes (`400` de validación,
  `409` de conflicto, `400/404/409/500` propagados desde core y `502` si core
  responde un status no resoluble).
- Tests unitarios de `CargoEtecService`, `AsignacionCargoService`,
  `GeneracionCargoLiquidacionService` y del interceptor de auditoría con JUnit 5 y
  Mockito.

### Cambiado

- `pom.xml`: versión del proyecto `0.0.1-SNAPSHOT` → `0.1.0`.
- `pom.xml`: se agregan `org.projectlombok:lombok` (scope `provided`) y
  `io.github.openfeign:feign-hc5`.
- `Application`: se habilita `@EnableFeignClients` sobre el paquete
  `um.haberes.etec.client`.

### Documentación

- `README.md`: badges de la tecnología declarada, funcionalidad, tabla de endpoints
  y publicación de diagramas.
- `docs/diagrams/arquitectura-general.mmd` y `docs/diagrams/despliegue.mmd`: los
  diagramas que el manifiesto de `generate-docs.yml` ya referenciaba.
