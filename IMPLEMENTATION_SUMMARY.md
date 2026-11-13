# Resumen de implementación – AlertaVía

Este documento resume qué se implementó en AlertaVía, cómo se relaciona con los diagramas de arquitectura y cómo se verificó.

## 1. Estado general

| Área | Estado |
|------|--------|
| Diagramas de arquitectura (modelo 4+1, Visual Paradigm) | ✅ 5 vistas en `.vpp` y PNG |
| Historias de usuario | ✅ 16 de 16 implementadas ([issues](https://github.com/Santiago425/AlertaVia-Diseno-de-Software/issues)) |
| Backend (Java 17 + Spring Boot 3) | ✅ API REST con JWT, MongoDB y servicios externos |
| Frontend (React 18 + Vite) | ✅ Pantallas de ciudadano y administrador |
| Pruebas automáticas | ✅ 42 pruebas (unitarias e integración con MongoDB real) |
| Integración continua | ✅ GitHub Actions: backend, frontend e imágenes Docker |
| Despliegue | ✅ Backend y base de datos en Render, frontend en Vercel |

## 2. Historias de usuario implementadas

| Sprint | Historias | Implementación principal |
|--------|-----------|--------------------------|
| Sprint 1 – Autenticación | HU-01 a HU-04 | `AuthController`, `AuthService`, `JwtTokenProvider`, `JwtAuthenticationFilter`, `SecurityConfig`, `AuthContext`, `ProtectedRoute` |
| Sprint 2 – Reportes | HU-05 a HU-09 | `ReportController`, `ReportService`, `LocationService`, `PhotoStorageService`, `Report`, `StatusChange`, `ReportFormPage`, `MyReportsPage` |
| Sprint 3 – Seguimiento y administración | HU-10 a HU-16 | `TrackingService`, `NotificationService`, `AdminController`, `NotificationController`, `AdminDashboardPage`, `StatisticsCharts` |

## 3. Correspondencia con los diagramas

| Vista | Cómo se refleja en el código |
|-------|------------------------------|
| 1. Contexto | Los actores y sistemas externos del diagrama son los usuarios (`Citizen`, `Administrator`) y los clientes de `integration` (`MapsClient`, `CloudStorageClient`, `PushNotificationClient`). |
| 2. Funcional | Cada componente es un servicio: Autenticación → `AuthService`, Reportes → `ReportService`, Seguimiento → `TrackingService`, Notificaciones → `NotificationService`, Ubicación → `LocationService`, Evidencias → `PhotoStorageService`. |
| 3. Lógica | Las clases de `model` son las del diagrama: `User` abstracta con herencia en `Citizen` y `Administrator`, composición de `Report` con `Location`, `Photo` y `StatusChange`, y las enumeraciones `ReportType` y `ReportStatus`. |
| 4. Desarrollo | Los paquetes del backend (`config`, `security`, `controller`, `dto`, `exception`, `service`, `integration`, `repository`, `model`) y del frontend (`pages`, `components`, `services`, `auth`) son los del diagrama. |
| 5. Despliegue | `frontend/vercel.json` (Vercel), `backend/Dockerfile` y `render.yaml` (Render con Docker – JVM 17) y MongoDB Atlas, con comunicación HTTPS :443 y MongoDB :27017. |

## 4. Modelo de datos en MongoDB

La aplicación usa **MongoDB** con **Spring Data MongoDB**. Cada clase raíz del dominio es un documento en su propia colección:

| Colección | Clase | Qué guarda |
|-----------|-------|------------|
| `users` | `User` → `Citizen` / `Administrator` | Ciudadanos y administradores en la misma colección; el campo `_class` (`CITIZEN` o `ADMIN`) indica el tipo, igual que el discriminador `tipo` del ejemplo de la editorial. |
| `reports` | `Report` | El reporte con su ubicación, fotos e historial **embebidos**. |
| `notifications` | `Notification` | Las notificaciones de cada ciudadano. |
| `mobility_entities` | `MobilityEntity` | Las entidades de movilidad a las que se asignan los reportes. |

**Decisiones de diseño:**
- **Composición → documentos embebidos.** `Location`, `Photo` y `StatusChange` solo existen dentro de un reporte, por eso se guardan dentro del documento `reports` y se leen en una sola consulta.
- **Asociación → referencia por id.** El reporte guarda `citizenId`, `reviewedById` y `mobilityEntityId` (y una copia del nombre para mostrarlo sin otra consulta), porque el ciudadano y la entidad tienen vida propia.
- **Herencia → una colección con discriminador.** `Citizen` y `Administrator` heredan de `User` y se guardan juntos en `users`.
- **Índices:** correo único en `users`, nombre único en `mobility_entities` e índices por estado, tipo y ciudadano en `reports`.

Ejemplo de documento en la colección `reports`:

```json
{
  "_id": "6730f1c2a9e4b5d8c1f2a3b4",
  "description": "Hueco de casi un metro frente al colegio",
  "type": "POTHOLE",
  "status": "IN_REVIEW",
  "createdAt": "2026-11-12T10:57:00",
  "location": { "latitude": 1.2136, "longitude": -77.2811, "address": "Calle 18 con Carrera 25, Pasto" },
  "citizenId": "6730f0a1...", "citizenName": "Ciudadano de prueba",
  "mobilityEntityId": "6730ef90...", "mobilityEntityName": "Secretaría de Infraestructura",
  "photos": [ { "url": "https://res.cloudinary.com/.../hueco.jpg", "uploadedAt": "2026-11-12T10:57:00" } ],
  "history": [
    { "previousStatus": null, "newStatus": "REPORTED", "comment": "Reporte creado por el ciudadano", "changedAt": "..." },
    { "previousStatus": "REPORTED", "newStatus": "IN_REVIEW", "comment": "Reporte verificado", "changedAt": "..." }
  ]
}
```

## 5. Decisiones de diseño

- **Arquitectura por capas:** el controlador solo recibe la petición, el servicio tiene la lógica y el repositorio accede a los datos.
- **Reglas del negocio en el modelo:** el flujo de estados vive en `ReportStatus.canChangeTo()` y `Report.updateStatus()`, por eso no se puede saltar de *Reportado* a *Resuelto* desde ningún punto del sistema.
- **Trazabilidad:** cada cambio de estado crea un `StatusChange` con estado anterior, estado nuevo, fecha y comentario.
- **Servicios externos aislados:** el paquete `integration` funciona como adaptador. Si Google Maps, Cloudinary o el correo no están configurados, el sistema sigue funcionando: usa las coordenadas como dirección, guarda las fotos en el servidor y deja la notificación solo en la aplicación.
- **Autenticación sin estado:** JWT firmado con HMAC-SHA; el backend no guarda sesiones en memoria.
- **DTO:** la API nunca expone los documentos de los reportes directamente; usa `ReportResponse` con el historial y las fotos.
- **Manejo de errores centralizado:** `GlobalExceptionHandler` devuelve mensajes en español con el código HTTP correcto (400, 401, 403, 404, 409 y 413).

## 6. Pruebas

| Clase de prueba | Tipo | Qué verifica |
|-----------------|------|--------------|
| `ReportTest` | Unitaria | Creación del reporte, flujo de estados, asignación de entidad, URL del mapa, notificaciones. |
| `ReportStatusTest` | Unitaria parametrizada | Las 11 transiciones permitidas y prohibidas y las etiquetas en español. |
| `AuthServiceTest` | Unitaria con Mockito | Registro con contraseña cifrada, correo duplicado, roles y usuario inexistente. |
| `TrackingServiceTest` | Unitaria con Mockito | Cambio de estado con notificación, comentario por defecto, transición inválida y asignación de entidad. |
| `PhotoStorageServiceTest` | Unitaria con Mockito | Mínimo una foto, máximo tres y solo imágenes. |
| `LocationServiceTest` | Unitaria con Mockito | Dirección escrita, dirección desde Google Maps y coordenadas como respaldo. |
| `AlertaViaApplicationTests` | Integración (MockMvc + MongoDB real con Testcontainers) | Recorrido completo por la API: registro, reporte con foto, permisos por rol, validación, asignación, filtros, estadísticas, notificaciones y credenciales incorrectas. |

Resultado: **42 pruebas, 0 fallos**.

## 7. Herramientas de desarrollo

| Herramienta | Uso |
|-------------|-----|
| `backend/mvnw` | Maven Wrapper: compila y ejecuta el backend sin instalar Maven. |
| `run.sh` | Levanta MongoDB, el backend y el frontend con un solo comando. |
| `docker-compose.yml` | Levanta el sistema completo en contenedores: MongoDB, mongo-express (`http://localhost:8081`), backend y frontend (`http://localhost:3000`). |
| `.devcontainer/` | Abre el proyecto listo para trabajar en VS Code o GitHub Codespaces (Java 17, Node 20 y Docker). |
| `.github/workflows/ci.yml` | Ejecuta las pruebas, compila el frontend y construye las imágenes Docker en cada push. |
| `.github/workflows/keep-alive.yml` | Mantiene activo el backend en Render durante el día. |

## 8. Despliegue

- **Base de datos:** MongoDB Atlas (clúster gratuito M0 en AWS).
- **Aplicación:** https://alertavia.vercel.app
- **API (Swagger):** https://alertavia-backend.onrender.com/swagger-ui.html
- **Cuentas de prueba:** `admin@alertavia.com` / `admin123` y `ciudadano@alertavia.com` / `ciudadano123`.

## 9. Mejoras futuras

- Notificaciones push en el celular con Firebase Cloud Messaging.
- Mapa de calor de los reportes por zona de la ciudad.
- Rol de funcionario de la entidad de movilidad para que actualice el avance directamente.
- Detección de reportes duplicados por cercanía y tipo de problema.
- Recuperación de contraseña por correo.
