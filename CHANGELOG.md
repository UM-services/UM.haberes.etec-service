# Changelog

Todos los cambios relevantes de este proyecto se documentan en este archivo.
El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/)
y el versionado [Semantic Versioning](https://semver.org/lang/es/).

## [0.7.0] - 2026-11-25

Séptima versión: la asignación de cargo transporta las horas del cargo y las veces
del básico de maestro de grado, y el control ETEC del período expone los parámetros
necesarios para calcularlos.

### Agregado

- `AsignacionCargoRequest` (`asignacion_cargo/infrastructure/web/dto`) acepta `horasCargo` y `vecesBasicoMaestroGrado` como `BigDecimal` opcionales, tanto en el alta como en la edición de la asignación.
- `EtecAsignacionCargoRequestDto` y `EtecAsignacionCargoDto` (`client/core/dto`) incorporan `horasCargo` y `vecesBasicoMaestroGrado` para enviar y recibir esos valores hacia `haberes-core-service`.
- `ControlEtecDto` incorpora `horasReferenciaCargo` (horas de referencia de los cargos por puntos del período) y `basicoMaestroGrado` (básico del maestro de grado del período), que devuelve `GET /api/haberes/etec/control/{anho}/{mes}/opcional`.

### Cambiado

- `AsignacionCargoService.toClientRequest` propaga `horasCargo` y `vecesBasicoMaestroGrado` al DTO de core en el alta y en la edición; no se agregaron validaciones nuevas, core sigue siendo el dueño de las reglas de esos campos.
- `pom.xml`: versión del proyecto `0.6.0` → `0.7.0`.

### Tests

- `AsignacionCargoServiceTest`: nuevo `enviaLasHorasYLasVecesDelCargoAlCore`, que captura el request enviado a core y verifica ambos valores.

### Documentación

- `README.md`: versión actual `0.7.0`; documenta los campos de horas y veces del básico en la asignación y los nuevos parámetros del control ETEC.

## [0.6.0] - 2026-11-25

Sexta versión: las llamadas Feign hacia `haberes-core-service` propagan el token
de servicio cuando core exige autenticación entre servicios.

### Agregado

- `FeignServiceTokenConfig` (`shared/security`): interceptor de request de Feign que agrega `Authorization: Bearer <token>` a las llamadas a core con el valor de la variable de entorno `HABERES_SERVICE_TOKEN` (el mismo que core declara en `HABERES_SERVICE_TOKENS` como `etec:<token>`). Si la cabecera ya viene en el template o el token no está configurado, no la modifica; sin token el servicio se comporta como antes.

### Cambiado

- `pom.xml`: versión del proyecto `0.5.0` → `0.6.0`.

### Documentación

- `README.md`: versión actual `0.6.0`; documenta la configuración de `HABERES_SERVICE_TOKEN` y la propagación del token en las llamadas Feign.
- `docs/diagrams/arquitectura-general.mmd`: el nodo de interceptores Feign incluye `FeignServiceTokenConfig` junto con `FeignAuditActorConfig`.

## [0.5.0] - 2026-11-24

Quinta versión: el control ETEC del período expone el monto fijo del cargo.

### Agregado

- `ControlEtecDto` incorpora `montoFijoCargo`, el importe que `haberes-core-service` suma a `puntos × valorPuntoEtec` al liquidar el cargo. `GET /api/haberes/etec/control/{anho}/{mes}/opcional` lo devuelve junto con `valorPuntoEtec`.

### Cambiado

- `pom.xml`: versión del proyecto `0.4.1` → `0.5.0`.

### Tests

- `ControlEtecEtecServiceTest` y `ControlEtecEtecControllerTest`: verifican que `montoFijoCargo` llega al response del control ETEC.

### Documentación

- `README.md`: versión actual `0.5.0`; el control ETEC documenta `valorPuntoEtec` y `montoFijoCargo`.

## [0.4.1] - 2026-11-23

Parche: el control ETEC del período deja de reenviar los headers del upstream.

### Corregido

- `GET /api/haberes/etec/control/{anho}/{mes}/opcional` deja de devolver el `ResponseEntity` de Feign: `ControlEtecEtecService` extrae el cuerpo y el controller arma un `ResponseEntity.ok(...)` propio. Copiar los headers hop-by-hop de core (`Transfer-Encoding`, `Connection`, `Keep-Alive`) hacía que el gateway rechazara la respuesta con `500`. Se conserva el `200` sin cuerpo cuando el período todavía no tiene control.

### Cambiado

- `pom.xml`: versión del proyecto `0.4.0` → `0.4.1`.

### Tests

- `ControlEtecEtecServiceTest` y `ControlEtecEtecControllerTest`: verifican la extracción del cuerpo, el `200` sin cuerpo y que la respuesta no copia headers del upstream.

### Documentación

- `README.md`: versión actual `0.4.1`.

## [0.4.0] - 2026-11-23

Cuarta versión: el catálogo y la generación ETEC resuelven los puntos del período.

### Agregado

- `GET /api/haberes/etec/cargo/{anho}/{mes}`: catálogo de cargos ETEC con los puntos vigentes en el período. Delega en `GET /api/haberes/core/categoria/puntos/{anho}/{mes}` mediante `CoreCategoriaClient.findConPuntosPorPeriodo`.
- `CargoEtecService.findAll(anho, mes)`, que expone el catálogo por período; `findAll()` conserva la plantilla actual para el ABM.

### Cambiado

- `GeneracionCargoLiquidacionService` valida y filtra con las categorías del período (`categoriasConPuntos(anho, mes)`) en lugar de la plantilla actual: un período histórico con puntos ya no se bloquea porque `categoria` cambió después. `findByLegajo` y `findByPeriodo` también filtran con los puntos del período.
- `pom.xml`: versión del proyecto `0.3.0` → `0.4.0`.

### Tests

- `CargoEtecServiceTest.listaElCatalogoConLosPuntosDelPeriodo` y ajustes en `GeneracionCargoLiquidacionServiceTest` para el endpoint por período.

### Documentación

- `README.md`: fila del endpoint de catálogo por período y versión actual `0.4.0`.

## [0.3.0] - 2026-11-22

Tercera versión: el circuito ETEC suma la pérdida de presentismo por asignación y
por horas, y expone el control ETEC del período. El CRUD y las validaciones siguen
viviendo en `haberes-core-service`; este servicio valida los requests propios y
delega por OpenFeign.

### Agregado

- Pérdida de presentismo por asignación (`/api/haberes/etec/presentismo`): consultas por legajo y período, por período, por asignación y período y por id; alta con `asignacionCargoId`, `anho`, `mes` y `motivo`; edición del `motivo`; y baja con `204`. Delega en el nuevo `CoreEtecPresentismoClient` (`/api/haberes/core/etec/presentismo/**`), dueño del CRUD y de las validaciones; `EtecPresentismoDto` devuelve además los datos de legajo, categoría, dependencia y persona.
- Pérdida de presentismo por horas (`/api/haberes/etec/presentismo-horas`): estado del legajo en el período (`tieneCargosHoras`), listado por período, consulta por id, alta con `legajoId`, `anho`, `mes` y `motivo`, edición del `motivo` y baja con `204`. Delega en el nuevo `CoreEtecPresentismoHorasClient`.
- Control ETEC del período (`GET /api/haberes/etec/control/{anho}/{mes}/opcional`): proxy de `CoreControlEtecClient` para que el frontend ETEC no llame directo a core; conserva el `200` sin cuerpo cuando el período no está configurado.
- `banner.txt`: banner ASCII del servicio al arrancar.
- Tests unitarios `PresentismoEtecServiceTest` y `PresentismoHorasEtecServiceTest`, que verifican la delegación en core y el mapeo de los requests del frontend a los DTO de core (`create`, `update` y `delete`).

### Cambiado

- `EtecExceptionHandler`: cuando core rechaza una escritura, extrae `detail`, `message` o `error` del cuerpo JSON antes de armar el `ProblemDetail`, en lugar de exponer el JSON crudo; si el cuerpo no es JSON o viene vacío, mantiene el comportamiento anterior.
- `pom.xml`: versión del proyecto `0.2.0` → `0.3.0`.

### Documentación

- `README.md`: funcionalidad y tablas de endpoints de presentismo, presentismo por horas y control ETEC; el detalle de errores de core documenta la extracción del mensaje JSON.
- `docs/diagrams/arquitectura-general.mmd`: los controllers y clientes Feign del diagrama incluyen presentismo, presentismo por horas y control ETEC.

## [0.2.0] - 2026-11-15

Segunda versión: la liquidación ETEC por puntos pasa a resolverla
`haberes-core-service`, dueño de la tabla y de la transacción, y este servicio
conserva las validaciones de negocio y la delegación.

### Cambiado

- `GeneracionCargoLiquidacionService` deja de calcular `puntos × valor` y de escribir `cargo_liquidacion`: `preview` y `generar` delegan en `POST /api/haberes/core/cargoliquidacion/generacion/etec/legajo/{legajoId}/{anho}/{mes}`, `POST .../etec/periodo/{anho}/{mes}`, `GET .../preview/etec/legajo/{legajoId}/{anho}/{mes}` y `GET .../preview/etec/periodo/{anho}/{mes}`. Core resuelve las asignaciones vigentes, el valor del punto, el monto fijo y el reemplazo idempotente del legajo/período. Se mantienen las rutas públicas de `/api/haberes/etec/cargo-liquidacion`, el `400` cuando no hay cargos con puntos configurados y el filtro de `findBy*` a las categorías con puntos.
- `GeneracionCargoLiquidacionService.validarPeriodo` deja de leer `control.valorPuntoEtec`: consulta el control propio del circuito por puntos en `GET /api/haberes/core/etec/control/{anho}/{mes}/opcional` con el nuevo `CoreControlEtecClient` y `ControlEtecDto`. Si el período no tiene control ETEC o el valor del punto no es mayor a cero responde el mismo `400` de negocio. Se eliminan `CoreControlClient` y `ControlDto`, que quedaban sin uso.
- `CoreCargoLiquidacionClient` suma `previewEtecByLegajo`, `previewEtecByPeriodo`, `generarEtecByLegajo` y `generarEtecByPeriodo`, y conserva `findByLegajo`, `findByPeriodo`, `replaceByPeriodo` y `replaceByLegajo`.
- `GeneracionCargoLiquidacionServiceTest` se reescribe para verificar la delegación en core, el mapeo de `CargoLiquidacionDto` a `CargoLiquidacionRequestDto`, la lectura del control ETEC opcional (período configurado y período sin valor de punto) y las dos validaciones de `400`.

### Documentación

- `README.md`: la liquidación documenta que `categoriaBasico = puntos × valor del punto + monto fijo` con los parámetros de `control_etec` y que la persistencia vive en core.
- `docs/diagrams/arquitectura-general.mmd`: el cliente Feign `coreControl` pasa a `coreControlEtec` y el diagrama incorpora `control_etec` entre las tablas de core.
- `AGENTS.md`: la regla de liquidación refleja que el cálculo y el reemplazo del período/legajo los ejecuta `haberes-core-service`.

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
