# Fintrack - Web API

## Resumen

Fintrack Web API es un servicio backend en Java/Spring Boot para gestionar finanzas personales. Permite registrar ingresos y egresos, generar presupuestos mensuales distribuidos por categorías y consultar historial y balances.

El proyecto sigue principios de Clean Architecture y Hexagonal (Ports & Adapters) para mantener bajo acoplamiento y facilitar pruebas.

## Arquitectura

### Visión general
Fintrack combina Clean Architecture y el patrón Hexagonal para separar responsabilidades y proteger la lógica de negocio de detalles de infraestructura. La comunicación entre capas se realiza mediante interfaces (ports) y adaptadores (adapters), favoreciendo testabilidad y reemplazo de componentes.

### Capas y responsabilidades
- `domain` (modelo y reglas): entidades de negocio (`Transaccion`, `Ingreso`, `Egreso`, `PresupuestoMensual`), enums (`Categoria`) y excepciones de dominio.
- `application` (casos de uso): orquesta las reglas de negocio. Contiene los `UseCase` (`TransaccionUseCase`, `ConsultaUseCase`, `BalanceUseCase`) y DTOs (commands/queries) que definen la API interna entre controladores y lógica.
- `adapters` (entrada/salida): controladores REST (`adapters/controller`) y manejadores comunes (`adapters/handler`) — traducen requests/responses a/desde DTOs.
- `infrastructure` (implementaciones): persistencia (JPA repositories, entities, mappers), servicios de seguridad (JWT), configuración (CORS, OpenAPI), y adaptadores que implementan los ports definidos en `domain.port.output`.

### Ports & Adapters
- Los ports (interfaces) en `domain.port.input` y `domain.port.output` definen contratos de entrada/salida para casos de uso.
- Implementaciones concretas están en `infrastructure.persistence.repository` y `infrastructure.dao` (JPA). Esto permite cambiar la tecnología de persistencia sin tocar casos de uso.

### DTOs y mapeo
- Los DTOs de `application.dto.commands` y `application.dto.queries` definen la estructura esperada por la API (ej. `IngresoDTO`, `EgresoDTO`, `BalanceDTO`, `MovimientoDTO`).
- El mapeo entre entidades JPA y objetos de dominio se realiza en `infrastructure.persistence.mapper.TransaccionMapper`.

### Flujo típico (ej. registrar ingreso)
1. El cliente hace `POST /api/transacciones/SaveIngreso` con `IngresoDTO`.
2. `TransaccionController` llama al `TransaccionUseCase`.
3. Caso de uso valida reglas (monto, fecha, porcentajes) y crea `Ingreso` y `PresupuestoMensual`.
4. El use case delega persistencia al `TransaccionRepositoryPort` (implementado por `MovimientoRepositoryImpl`).
5. Respuesta se convierte a `PresupuestoResponseDTO` y se retorna al cliente.

## Estructura del proyecto (detallada)

Raíz: `src/main/java/com/example/fintrack_webapi`

- adapters/
  - controller/  → REST controllers (`TransaccionController`, `HistorialController`)
  - handler/     → manejo centralizado de errores (`GlobalExceptionHandler`, `ErrorResponse`, `FieldErrorDetail`)

- application/
  - dto/
    - commands/ → `IngresoDTO`, `EgresoDTO`
    - queries/  → `MovimientoDTO`, `BalanceDTO`, `PresupuestoResponseDTO`
  - usecase/     → implementaciones de casos de uso (`TransaccionUseCase`, `ConsultaUseCase`, `BalanceUseCase`)

- domain/
  - model/       → entidades de dominio y `Categoria` (enumeración de códigos)
  - port/        → `input` (interfaces expuestas por la capa de aplicación) y `output` (contratos de persistencia)
  - exception/   → excepciones de dominio (`BadRequestException`, `ResourceNotFoundException`)

- infrastructure/
  - config/      → `SecurityConfig`, `OpenApiConfig`, `WebConfig`
  - dao/         → JPA Repositories con consultas nativas (`MovimientoJpaRepository`, `BalanceJpaRepository`, `IngresoJpaRepository`, `EgresoJpaRepository`)
  - persistence/
    - entity/     → JPA entities (`IngresoEntity`, `EgresoEntity`, `MovimientoEntity`)
    - mapper/     → mappers entre entities y dominio (`TransaccionMapper`)
    - repository/ → implementaciones de ports (ej. `MovimientoRepositoryImpl`, `BalanceRepositoryImpl`)
  - security/    → JWT helpers y filtros (`JwtService`, `JwtAuthenticationFilter`)

La separación facilita encontrar la responsabilidad de cada clase y reduce el acoplamiento entre lógica de negocio y detalle de infraestructura.

## Manejo de errores

La API centraliza las respuestas de error en `adapters.handler.GlobalExceptionHandler` que construye objetos `ErrorResponse` con la siguiente información:

- `timestamp` (fecha/hora)
- `status` (código HTTP)
- `error` (razón HTTP)
- `message` (mensaje legible)
- `path` (URI solicitada)
- `traceId` (UUID generado por petición para rastreo)
- `details` (opcional) — lista de `FieldErrorDetail` con `field` y `message` cuando aplica (validaciones de campos)

Excepciones tratadas y códigos devueltos (por `GlobalExceptionHandler`):
- `BadRequestException` → 400 Bad Request
- `ResourceNotFoundException` → 404 Not Found
- `MethodArgumentNotValidException` → 400 Bad Request (con `details` por campo)
- `MethodArgumentTypeMismatchException` → 400 Bad Request
- `AccessDeniedException` → 403 Forbidden
- `Exception` (catch-all) → 500 Internal Server Error

Ejemplo de respuesta de validación (HTTP 400):

```json
{
  "timestamp": "2026-05-07T12:34:56.789",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación",
  "path": "/api/transacciones/egreso",
  "traceId": "a1b2c3d4-...",
  "details": [
    { "field": "monto", "message": "El monto es obligatorio" },
    { "field": "fecha", "message": "La fecha es obligatoria" }
  ]
}
```

El `traceId` facilita correlación en logs y soporte. `GlobalExceptionHandler` genera este UUID automáticamente.

Se recomienda que los clientes validen los DTOs antes de llamar a la API y que capturen y muestren `message` y `details` para una mejor UX.

## Tecnologías

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Hibernate
- Jakarta Validation (Bean Validation)
- Lombok
- OpenAPI / Swagger

## Requisitos

- JDK 21
- Maven 3.8+
- Base de datos PostgreSQL
- Variables de entorno (ver sección de configuración)

## Configuración (variables de entorno)

- `DB_URL` → URL JDBC de PostgreSQL (ej. `jdbc:postgresql://host:5432/fintrack`)
- `DB_USER` → usuario de la base de datos
- `DB_PASSWORD` → contraseña de la base de datos
- `JWT_SECRET` → secreto en Base64 para validar JWT
- `PORT` → opcional, puerto del servidor (por defecto 8080)

Las propiedades están referenciadas en `src/main/resources/application.properties`.

## Construir y ejecutar

En Windows (mvnw):

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

Con Maven instalado:

```bash
mvn clean package
mvn spring-boot:run
```

También existe un `Dockerfile` para producción; ajustar variables de entorno al crear la imagen/contenedor.

## Endpoints principales (resumen)

Autenticación: la API usa JWT. Agregar header `Authorization: Bearer <token>` en las rutas protegidas.

- POST /api/transacciones/SaveIngreso  — Registrar un ingreso y generar presupuesto
  - Request (JSON):
    - `monto` (Double) — >= 200
    - `fecha` (String) — formato `yyyy-MM-dd` (debe estar entre -1 año y hoy)
    - `porcentajes` (Map<Integer,Double>) — map de códigos de categoría a porcentaje; la suma debe ser 100
  - Respuesta: 201 Created (cuerpo: `PresupuestoResponseDTO` con distribución por categoría)

- POST /api/transacciones/egreso  — Registrar un egreso
  - Request (JSON):
    - `monto` (Double) — >= 1
    - `fecha` (String) — `yyyy-MM-dd`
    - `codigoCategoria` (int) — códigos válidos: 1..6
    - `descripcion` (String)
  - Respuesta: 201 Created

- GET /api/consultas/historial  — Obtener histórico completo de movimientos
  - Respuesta: 200 OK — lista de `MovimientoDTO` (tipo, monto, fecha, categoría, descripción)

- GET /api/consultas/ultimos?cantidad={n}  — Obtener los n últimos movimientos (1 ≤ n ≤ 20)

- GET /api/consultas/por-categoria?codigoCategoria={codigo}  — Movimientos filtrados por categoría

- GET /api/consultas/balance?fecha={yyyy-MM-dd}  — Balance del mes (si no se pasa fecha, mes actual)
  - Respuesta: `BalanceDTO` (totalIngresos, totalGastos, balance, %gastos, %ahorro)

La documentación OpenAPI/Swagger está configurada y disponible en `/v3/api-docs` y la UI de Swagger (`/swagger-ui.html` o `/swagger-ui/index.html`).

## Validaciones y reglas importantes

- Fechas deben usar `yyyy-MM-dd` y estar en el rango permitido (último año hasta hoy) — validado en `TransaccionUseCase`.
- Ingresos requieren `monto >= 200` y porcentajes que sumen ~100% (tolerancia 0.1).
- Egresos requieren `monto >= 1` y `codigoCategoria` válido.
- Si un parámetro es inválido, la API responde con códigos HTTP apropiados (400 para validación, 401/403 para autenticación/autorización, etc.). El manejo global de errores está en `adapters/handler/GlobalExceptionHandler.java`.

## Base de datos y persistencia

Tablas principales utilizadas por la aplicación:

- `ingreso` — ingresos (id, monto, fecha)
- `egreso` — egresos (id, monto, fecha, idcat, descripcion)
- `movimiento` — vista / tabla auxiliar que referencia transferencias (tipotransferencia, idtransferencia)
- `categoria` — catálogo de categorías (se mapean con `domain.model.Categoria`)

La lógica de lectura de movimientos usa consultas nativas (ver `MovimientoJpaRepository`) y el repositorio `MovimientoRepositoryImpl` construye los objetos de dominio a partir de las entidades.

## Seguridad

- JWT: ver `infrastructure/security/JwtService` y `JwtAuthenticationFilter`. La app espera tokens firmados con la clave configurada en `JWT_SECRET`.
- CORS: la configuración permite por defecto el origen `https://fintrack-frontend-rho.vercel.app` en `SecurityConfig` y `WebConfig` contiene un CORS más abierto (ajustar según despliegue).

## Códigos de categoría

La enumeración `Categoria` define los códigos usados por la API:

- `SERVICIOS` = 1
- `ENTRETENIMIENTO` = 2
- `TRANSPORTE` = 3
- `ALIMENTACION` = 4
- `SALUD` = 5
- `DEUDAS` = 6

## Estructura relevante (ubicaciones)

- Casos de uso: `src/main/java/com/example/fintrack_webapi/application/usecase`
- Controladores (API): `src/main/java/com/example/fintrack_webapi/adapters/controller`
- Modelos de dominio: `src/main/java/com/example/fintrack_webapi/domain/model`
- Repositorios JPA: `src/main/java/com/example/fintrack_webapi/infrastructure/dao`

