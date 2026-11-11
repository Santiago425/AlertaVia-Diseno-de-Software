# 🚧 **AlertaVía – Plataforma de Reporte y Seguimiento de Problemas Viales**

**Descripción del Proyecto**
AlertaVía es una aplicación web que permite a los ciudadanos reportar problemas en las vías de la ciudad, como huecos, semáforos dañados, señalización deteriorada e inundaciones, adjuntando fotografías y la ubicación del lugar. La plataforma organiza los reportes, los asigna a la entidad de movilidad encargada y permite hacer seguimiento a su estado hasta que el problema queda resuelto.

El sistema permite:

* Registrar reportes con tipo de problema, descripción, fotos y ubicación GPS.
* Obtener la dirección del reporte a partir de las coordenadas con Google Maps.
* Consultar el estado de cada reporte y su historial de cambios.
* Que el administrador valide o rechace los reportes, cambie su estado y los asigne a la entidad de movilidad encargada.
* Notificar al ciudadano cada vez que su reporte cambia de estado o es asignado.
* Consultar estadísticas de reportes por estado y por tipo de problema.

La aplicación busca mejorar la comunicación entre la ciudadanía y las entidades de movilidad, con reportes completos, trazabilidad de cada cambio y transparencia sobre la atención de los problemas viales.

## **Objetivo General**
Facilitar la comunicación entre la ciudadanía y las entidades encargadas de la malla vial, centralizando los reportes de problemas viales en una sola plataforma con seguimiento transparente de su atención.

## **Funcionalidades Principales**

* Registro e inicio de sesión de ciudadanos y administradores con JWT.
* Creación de reportes con tipo de problema, descripción, hasta 3 fotos y ubicación.
* Conversión de coordenadas a dirección con la API de Google Maps.
* Almacenamiento de las fotos en Cloudinary.
* Flujo de estados controlado: Reportado → En revisión → En proceso → Resuelto (o Rechazado).
* Historial de cambios de cada reporte.
* Asignación de reportes a las entidades de movilidad.
* Notificaciones al ciudadano en la aplicación y por correo electrónico.
* Panel administrativo con estadísticas y filtros por estado y tipo.
* Arquitectura por capas en Spring Boot con PostgreSQL, frontend en React y despliegue en Render y Vercel.

## **Aplicación Desplegada**

* **Aplicación web:** https://alertavia.vercel.app
* **API (Swagger):** https://alertavia-backend.onrender.com/swagger-ui.html
* **Repositorio:** https://github.com/Santiago425/AlertaVia-Diseno-de-Software

**Cuentas de prueba:**

| Rol | Usuario | Contraseña |
|-----|---------|------------|
| Administrador | `admin@alertavia.com` | `admin123` |
| Ciudadano | `ciudadano@alertavia.com` | `ciudadano123` |

## **Documentación del Proyecto (SRS)**

1. [Introducción](1).-Introduccion)
2. [Descripción General](2).-Descripcion-General)
3. [Requerimientos de Interfaces Externas](3).-Requerimientos-de-Interfaces-Externas)
4. [Historias de Usuario](4).-Historias-de-Usuario)
5. [Requerimientos no funcionales](5).-Requerimientos-no-funcionales)
6. [Diagramas de Arquitectura](6).-Diagramas-de-Arquitectura)
7. [Implementación y Despliegue](7).-Implementacion-y-Despliegue)

## **Autor**

Santiago Campoverde

**Información del Curso**

* Materia: Diseño de Software
* Universidad Cooperativa de Colombia
* Período Académico: 2026-2
