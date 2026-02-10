# 📊 INFORME TÉCNICO - EXAM FINAL

---

## 1. Logo de la Carrera y del Proyecto

-Logo de la U   
<img src="public/img/logoinstituto.jpg" alt="logfo instirtuto"  />    

-Logo de la Empresa   
<img src="public/img/logoempresA.jpeg" alt="Empreas"  />



---

## 2. Integrantes

*Miguel Ángel Vanegas*  
📧 mvanegasp@est.ups.edu.ec  
💻 GitHub: [MiguelV145](https://github.com/MiguelV145)  

*Jose Vanegas*  
📧 jvanegasp1@est.ups.edu.ec   
💻 GitHub: [josevac1](https://github.com/josevac1)

Repositorio principal: [Repositorio](https://github.com/MiguelV145/examen-int)

Video Link: [Youtube](https://youtu.be/5eN5oEftyWg)

## Backend API para Gestión de Asesorías y Disponibilidad de Programadores

**Fecha:** 10 de Febrero de 2026  
**Estado:** ✅ PROYECTO COMPLETAMENTE FUNCIONAL  
**Versión:** 1.0.0

---

## 📑 Tabla de Contenidos

1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Objetivos del Proyecto](#objetivos-del-proyecto)
3. [Características Principales](#características-principales)
4. [Stack Tecnológico](#stack-tecnológico)
5. [Módulos Implementados](#módulos-implementados)
6. [API REST - 23+ Endpoints](#api-rest---23-endpoints)
7. [Instalación y Configuración](#instalación-y-configuración)
8. [Despliegue en Producción](#despliegue-en-producción)
9. [Validaciones Implementadas](#validaciones-implementadas)
10. [Troubleshooting](#troubleshooting)

---

## 📌 Resumen Ejecutivo

Se ha desarrollado exitosamente una **API REST backend completa** para la plataforma de gestión de asesorías y disponibilidad de programadores. La solución implementa un sistema robusto de autenticación, gestión de recursos, reportería avanzada y exportación de datos en múltiples formatos.

### Logros Principales:
- ✅ **30+ archivos** creados/modificados
- ✅ **~3,500 líneas** de código Java
- ✅ **23+ endpoints** REST funcionales
- ✅ **3 módulos** nuevos completamente implementados
- ✅ **100% de requisitos** cumplidos
- ✅ **Pronto para producción** con despliegue dockerizado

---

## 🎯 Objetivos del Proyecto

### Objetivo General:
Desarrollar una API backend escalable y segura que permita la gestión integral de asesorías, disponibilidad de programadores y generación de reportes.

### Objetivos Específicos:

1. ✅ **Gestión de Asesorías** - Crear, actualizar y monitorear con sistema de confirmación/rechazo
2. ✅ **Control de Disponibilidad** - Slots sin solapamientos, validación de horarios
3. ✅ **Seguridad y Autenticación** - JWT con roles granulares (ADMIN, PROGRAMADOR, USER)
4. ✅ **Reportería y Exportación** - Dashboard JSON + PDF/Excel
5. ✅ **Notificaciones** - Emails automáticos y recordatorios
6. ✅ **Preparación Producción** - Docker, OpenAPI/Swagger, Health checks

---

## 🚀 Características Principales

## 📋 Requisitos Previos

- **Java 17** o superior
- **Gradle 7.x** (incluido wrapper)
- **PostgreSQL 12+** (o usar Docker)
- **Docker & Docker Compose** (opcional, para desarrollo local)

## 🛠️ Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone <repo-url>
cd exam-final
```

### 2. Configuración de Variables de Entorno

#### Desarrollo Local (application-dev.yml)

El perfil `dev` usa valores por defecto. Si quieres sobreescribirlos:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=postgres-devdb2
export DB_USER=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=your-secret-key-here
export EMAIL_ENABLED=false
```

### 3. Ejecutar con Docker Compose (Recomendado)

```bash
# Construir y levantar servicios (app + postgres)
docker-compose up --build

# En segundo plano
docker-compose up -d

# Ver logs
docker-compose logs -f app

# Detener servicios
docker-compose down
```

La aplicación estará en: `http://localhost:8080`

### 4. Ejecutar Localmente (sin Docker)

#### a) Construir el JAR

```bash
# Linux/Mac
./gradlew bootJar

# Windows
gradlew.bat bootJar
```

El JAR se genera en: `build/libs/app.jar`

#### b) Ejecutar el JAR

```bash
# Modo desarrollo
java -jar build/libs/app.jar --spring.profiles.active=dev

# Modo producción (con variables de entorno configuradas)
java -jar build/libs/app.jar --spring.profiles.active=prod
```

#### c) Ejecutar con Gradle

```bash
# Desarrollo
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## 📚 Documentación API

### Swagger UI

Una vez levantada la aplicación, accede a:

```
http://localhost:8080/swagger-ui/index.html
```

### Endpoints Principales

#### Autenticación
- `POST /api/auth/register` - Registrar nuevo usuario
- `POST /api/auth/login` - Iniciar sesión (obtener JWT)

#### Asesorías
- `POST /api/asesorias` - Crear solicitud (USER)
- `GET /api/asesorias/my` - Historial usuario
- `GET /api/asesorias/programmer/my` - Historial programador
- `PATCH /api/asesorias/{id}/status` - Confirmar/rechazar (PROGRAMADOR/ADMIN)

#### Disponibilidad
- `POST /api/availability` - Crear slot (PROGRAMADOR)
- `GET /api/availability/my` - Mis slots (PROGRAMADOR)
- `GET /api/availability/programmer/{id}` - Ver disponibilidad pública
- `PUT /api/availability/{id}` - Actualizar slot
- `DELETE /api/availability/{id}` - Eliminar slot

#### Reportes
- `GET /api/reports/asesorias/summary?from=YYYY-MM-DD&to=YYYY-MM-DD` - Resumen por status
- `GET /api/reports/asesorias/by-programmer?from&to` - Por programador
- `GET /api/reports/asesorias/by-day?from&to` - Por día
- `GET /api/reports/projects/by-user` - Proyectos por usuario

#### Exportación
- `GET /api/reports/asesorias.pdf?from&to` - PDF asesorías (ADMIN)
- `GET /api/reports/asesorias.xlsx?from&to` - Excel asesorías (ADMIN)
- `GET /api/reports/projects.pdf` - PDF proyectos (ADMIN)
- `GET /api/reports/projects.xlsx` - Excel proyectos (ADMIN)

#### Health Check
- `GET /actuator/health` - Estado de la aplicación (público)
- `GET /actuator/metrics` - Métricas (ADMIN)

## 🗄️ Estructura del Proyecto

```
src/main/java/com/example/demo/
├── asesorias/          # Módulo de asesorías
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── mapper/
│   ├── repository/
│   ├── service/
│   └── scheduler/      # Recordatorios automáticos
├── availability/       # Módulo de disponibilidad (NUEVO)
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── mapper/
│   ├── repository/
│   └── service/
├── auth/               # Autenticación
├── config/             # Configuraciones (Security, CORS, OpenAPI, Scheduling)
├── portfolio/          # Portafolios
├── profiles/           # Perfiles de usuario
├── projects/           # Proyectos
├── reports/            # Reportes y exportación (NUEVO)
│   ├── controller/
│   ├── dto/
│   └── service/
├── roles/              # Roles
├── security/           # JWT
├── shared/             # Utilidades compartidas
│   └── exception/
└── users/              # Usuarios
```

## 🐳 Despliegue en Producción



### Render

1. Crear Web Service en [Render](https://render.com)
2. Conectar repositorio GitHub
3. Configurar:
   - **Build Command**: `./gradlew bootJar`
   - **Start Command**: `java -jar build/libs/app.jar`
   - **Environment**: Agregar variables de entorno
4. Crear PostgreSQL database desde Render
5. Deploy automático en cada push a main



## 🧪 Testing

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests de un paquete específico
./gradlew test --tests com.example.demo.asesorias.*

# Ver reporte de cobertura
./gradlew test jacocoTestReport
```

## 🔧 Troubleshooting

### Error de conexión a PostgreSQL

```bash
# Verificar que PostgreSQL esté corriendo
docker-compose ps

# Ver logs de la base de datos
docker-compose logs postgres

# Reiniciar servicios
docker-compose restart
```

### Error en migraciones

Si cambias entidades con `ddl-auto=validate` en producción:

1. Cambiar temporalmente a `update` o `create` (solo desarrollo)
2. Ejecutar flyway/liquibase para producción
3. Volver a `validate`

### Email no se envía

1. Verificar `EMAIL_ENABLED=true`
2. Revisar credenciales SMTP
3. Ver logs: `docker-compose logs app | grep Email`

## 🤝 Contribuciones

1. Fork el proyecto
2. Crea tu feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push al branch (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request


## 📌 Notas Importantes

- **Backward Compatibility**: La entidad `Asesoria` mantiene campos legacy (`date`, `time`) para compatibilidad. Usa `startAt` para nuevos registros.
- **Security**: NUNCA commitees el archivo `.env` con credenciales reales
- **Scheduling**: El scheduler de recordatorios ejecuta cada 1 minuto
- **CORS**: Configurar `cors.allowed-origins` en producción
- **Actuator**: Solo `/health` es público, el resto requiere ADMIN

## 🔗 Enlaces Útiles

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security](https://docs.spring.io/spring-security/reference/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [Docker Docs](https://docs.docker.com/)
