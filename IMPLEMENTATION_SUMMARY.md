# Resumen de implementación – AlertaVía

Este documento resume qué se implementó en AlertaVía, cómo se relaciona con los diagramas de arquitectura y cómo se verificó.

## 1. Estado general

| Área | Estado |
|------|--------|
| Diagramas de arquitectura (modelo 4+1, Visual Paradigm) | ✅ 5 vistas en `.vpp` y PNG |
| Historias de usuario | ✅ 16 de 16 implementadas ([issues](https://github.com/Santiago425/AlertaVia-Diseno-de-Software/issues)) |
| Backend (Java 17 + Spring Boot 3) | ✅ API REST con JWT, PostgreSQL y servicios externos |
| Frontend (React 18 + Vite) | ✅ Pantallas de ciudadano y administrador |
| Pruebas automáticas | ✅ 39 pruebas (unitarias e integración) |
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
| 5. Despliegue | `frontend/vercel.json` (Vercel), `backend/Dockerfile` y `render.yaml` (Render con Docker – JVM 17 y PostgreSQL 16), con comunicación HTTPS :443 y JDBC :5432. |

## 4. Decisiones de diseño

- **Arquitectura por capas:** el controlador solo recibe la petición, el servicio tiene la lógica y el repositorio accede a los datos.
- **Reglas del negocio en el modelo:** el flujo de estados vive en `ReportStatus.canChangeTo()` y `Report.updateStatus()`, por eso no se puede saltar de *Reportado* a *Resuelto* desde ningún punto del sistema.
- **Trazabilidad:** cada cambio de estado crea un `StatusChange` con estado anterior, estado nuevo, fecha y comentario.
- **Servicios externos aislados:** el paquete `integration` funciona como adaptador. Si Google Maps, Cloudinary o el correo no están configurados, el sistema sigue funcionando: usa las coordenadas como dirección, guarda las fotos en el servidor y deja la notificación solo en la aplicación.
- **Autenticación sin estado:** JWT firmado con HMAC-SHA; el backend no guarda sesiones en memoria.
- **DTO:** la API nunca expone las entidades JPA directamente en los reportes; usa `ReportResponse` con el historial y las fotos.
- **Manejo de errores centralizado:** `GlobalExceptionHandler` devuelve mensajes en español con el código HTTP correcto (400, 401, 403, 404 y 413).

## 5. Pruebas

| Clase de prueba | Tipo | Qué verifica |
|-----------------|------|--------------|
| `ReportTest` | Unitaria | Creación del reporte, flujo de estados, asignación de entidad, URL del mapa, notificaciones. |
| `ReportStatusTest` | Unitaria parametrizada | Las 11 transiciones permitidas y prohibidas y las etiquetas en español. |
| `AuthServiceTest` | Unitaria con Mockito | Registro con contraseña cifrada, correo duplicado, roles y usuario inexistente. |
| `TrackingServiceTest` | Unitaria con Mockito | Cambio de estado con notificación, comentario por defecto, transición inválida y asignación de entidad. |
| `PhotoStorageServiceTest` | Unitaria con Mockito | Mínimo una foto, máximo tres y solo imágenes. |
| `LocationServiceTest` | Unitaria con Mockito | Dirección escrita, dirección desde Google Maps y coordenadas como respaldo. |
| `AlertaViaApplicationTests` | Integración (MockMvc + H2) | Recorrido completo por la API: registro, reporte con foto, permisos por rol, validación, asignación, filtros, estadísticas, notificaciones y credenciales incorrectas. |

Resultado: **39 pruebas, 0 fallos**.

## 6. Herramientas de desarrollo

| Herramienta | Uso |
|-------------|-----|
| `backend/mvnw` | Maven Wrapper: compila y ejecuta el backend sin instalar Maven. |
| `run.sh` | Levanta PostgreSQL, el backend y el frontend con un solo comando. |
| `docker-compose.yml` | Levanta el sistema completo en contenedores (base de datos, backend y frontend en `http://localhost:3000`). |
| `.devcontainer/` | Abre el proyecto listo para trabajar en VS Code o GitHub Codespaces (Java 17, Node 20 y Docker). |
| `.github/workflows/ci.yml` | Ejecuta las pruebas, compila el frontend y construye las imágenes Docker en cada push. |
| `.github/workflows/keep-alive.yml` | Mantiene activo el backend en Render durante el día. |

## 7. Despliegue

- **Aplicación:** https://alertavia.vercel.app
- **API (Swagger):** https://alertavia-backend.onrender.com/swagger-ui.html
- **Cuentas de prueba:** `admin@alertavia.com` / `admin123` y `ciudadano@alertavia.com` / `ciudadano123`.

## 8. Mejoras futuras

- Notificaciones push en el celular con Firebase Cloud Messaging.
- Mapa de calor de los reportes por zona de la ciudad.
- Rol de funcionario de la entidad de movilidad para que actualice el avance directamente.
- Detección de reportes duplicados por cercanía y tipo de problema.
- Recuperación de contraseña por correo.
