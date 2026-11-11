# AlertaVía – Plataforma de Reporte y Seguimiento de Problemas Viales

AlertaVía es una aplicación web para que los ciudadanos reporten problemas en las vías de la ciudad (huecos, semáforos dañados, señalización deteriorada e inundaciones) adjuntando una foto y la ubicación del lugar. La plataforma organiza los reportes, los asigna a la entidad de movilidad encargada y permite hacer seguimiento a su estado hasta que el problema queda resuelto.

**[Ver la Wiki del proyecto](https://github.com/Santiago425/AlertaVia-Diseno-de-Software/wiki)**

## Funcionalidades principales

- Registro e inicio de sesión de ciudadanos y administradores (JWT).
- Creación de reportes con tipo de problema, descripción, foto y ubicación GPS.
- Ubicación del reporte en el mapa y conversión de coordenadas a dirección.
- Seguimiento del estado del reporte: Reportado, En revisión, En proceso, Resuelto o Rechazado.
- Historial de cambios de estado de cada reporte.
- Panel administrativo para validar reportes, cambiar su estado y asignarlos a una entidad de movilidad.
- Notificaciones al ciudadano cuando su reporte cambia de estado.
- Estadísticas de reportes para el administrador.

## Tecnologías

| Capa | Tecnología |
|------|------------|
| Frontend | React + Vite (desplegado en Vercel) |
| Backend | Java 17 + Spring Boot, API REST con JWT (desplegado en Render) |
| Base de datos | PostgreSQL |
| Mapas y geolocalización | Google Maps API |
| Almacenamiento de fotos | Cloudinary |
| Notificaciones | Firebase Cloud Messaging / SMTP |
| Modelado | Visual Paradigm |

## Vistas de arquitectura (modelo 4+1)

| Vista | Pregunta que responde | Diagrama |
|-------|-----------------------|----------|
| 1. Contexto | ¿Con quién se comunica el sistema? | [Imagen](diagramas/imagenes/vista-1-contexto.png) · [.vpp](diagramas/vpp/vista-1-contexto.vpp) |
| 2. Funcional | ¿Qué componentes tiene y qué servicios ofrece cada uno? | [Imagen](diagramas/imagenes/vista-2-funcional.png) · [.vpp](diagramas/vpp/vista-2-funcional.vpp) |
| 3. Lógica | ¿Qué clases maneja el sistema y cómo se relacionan? | [Imagen](diagramas/imagenes/vista-3-logica.png) · [.vpp](diagramas/vpp/vista-3-logica.vpp) |
| 4. Desarrollo | ¿Cómo se organiza el código en paquetes y capas? | [Imagen](diagramas/imagenes/vista-4-desarrollo.png) · [.vpp](diagramas/vpp/vista-4-desarrollo.vpp) |
| 5. Despliegue | ¿Dónde se ejecuta cada parte del sistema? | [Imagen](diagramas/imagenes/vista-5-despliegue.png) · [.vpp](diagramas/vpp/vista-5-despliegue.vpp) |

El archivo [`AlertaVia.vpp`](diagramas/vpp/AlertaVia.vpp) contiene las cinco vistas en un solo proyecto. La explicación de cada diagrama está en la página **Diagramas de Arquitectura** de la wiki.

### Vista 1 – Contexto
![Vista de contexto](diagramas/imagenes/vista-1-contexto.png)

### Vista 2 – Funcional
![Vista funcional](diagramas/imagenes/vista-2-funcional.png)

### Vista 3 – Lógica
![Vista lógica](diagramas/imagenes/vista-3-logica.png)

### Vista 4 – Desarrollo
![Vista de desarrollo](diagramas/imagenes/vista-4-desarrollo.png)

### Vista 5 – Despliegue
![Vista de despliegue](diagramas/imagenes/vista-5-despliegue.png)

## Estructura del repositorio

```
AlertaVia-Diseno-de-Software/
├── README.md
├── diagramas/
│   ├── vpp/          Proyectos de Visual Paradigm (uno por vista y uno con todas)
│   └── imagenes/     Diagramas exportados en PNG
└── wiki/             Copia de las páginas de la wiki
```

## Cómo abrir los diagramas

1. Instalar Visual Paradigm (la edición Community es suficiente).
2. Abrir cualquier archivo `.vpp` de la carpeta `diagramas/vpp` con doble clic o desde **Project → Open**.

## Autor

**Santiago Campoverde**
Universidad Cooperativa de Colombia – Diseño de Software
