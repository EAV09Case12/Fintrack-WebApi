# Fintrack - Web API

## Descripción

Fintrack es un microservicio backend desarrollado para la gestión de finanzas personales, permitiendo registrar ingresos, egresos y generar presupuestos mensuales basados en la distribución por categorías.

---

## Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Hibernate
- Jakarta Validation
- Lombok
- Swagger / OpenAPI

---

## Arquitectura

El proyecto sigue un enfoque híbrido entre:

### Clean Architecture
- Separación clara entre:
  - `domain` → lógica de negocio
  - `application` → casos de uso
  - `infrastructure` → persistencia
  - `adapters` → controladores y handlers

### Arquitectura Hexagonal (Ports & Adapters)
- Uso de **ports** (interfaces) para desacoplar la lógica de negocio
- Implementaciones en infraestructura conectadas mediante **adapters**

Esto permite:
- Alta mantenibilidad
- Bajo acoplamiento
- Facilidad de testing
- Escalabilidad hacia microservicios independientes

---

## Funcionalidades implementadas (Sprint actual)

### Registro de ingresos
- Creación de ingreso
- Generación automática de presupuesto mensual
- Validación de porcentajes (deben sumar 100%)

### Registro de egresos
- Asociación a categoría válida
- Persistencia en base de datos
- Generación automática de movimientos mediante triggers

### Consultas
- Historial de movimientos
- Últimos movimientos
- Movimientos por categoría

### Validaciones implementadas
- Formato de fecha obligatorio (`yyyy-MM-dd`)
- Fechas dentro de rango válido (último año hasta hoy)
- Validaciones de DTO con Jakarta Validation
- Validación de reglas de negocio (porcentajes, categorías)

### Manejo de errores
- Implementación de `GlobalExceptionHandler`
- Respuestas HTTP estandarizadas
- Manejo de:
  - 400 → errores de validación
  - 404 → recursos no encontrados
  - 422/400 → reglas de negocio inválidas
  - 500 → errores inesperados

---

## Estructura del proyecto
src/main/java/com/example/fintrack_webapi
│
├── adapters
│ ├── controller
│ └── handler
│
├── application
│ ├── dto
│ └── usecase
│
├── domain
│ ├── model
│ ├── port
│ └── exception
│
├── infrastructure
│ ├── persistence
│ ├── dao
│ └── config

---

## Endpoints principales

### Transacciones
- `POST /api/transacciones/SaveIngreso`
- `POST /api/transacciones/egreso`

### Consultas
- `GET /api/consultas/historial`
- `GET /api/consultas/ultimos?cantidad=5`
- `GET /api/consultas/por-categoria?codigoCategoria=1`

---

## Notas

- Los movimientos se generan automáticamente mediante triggers en base de datos.
- Las validaciones de negocio se manejan en la capa de aplicación.
- Los errores están centralizados en un handler global.

---

## Próximos pasos

- Implementación de frontend básico
- Mejora en estandarización de errores (códigos internos)
- Tests unitarios y de integración
- Despliegue como microservicio independiente

---

## Equipo

Proyecto desarrollado como parte de un proceso académico en Ingeniería de Sistemas.
