# Exam Final - Backend Spring Boot

API RESTful para gestión de portafolios, proyectos, asesorías y disponibilidad de programadores. Implementa autenticación JWT, notificaciones por correo, reportes exportables y sistema de scheduling.

## 🚀 Características

- ✅ **Autenticación y Autorización**: JWT con roles (ADMIN, PROGRAMADOR, USER)
- ✅ **Gestión de Portafolios y Proyectos**: CRUD completo con ownership
- ✅ **Sistema de Asesorías**: Solicitud, confirmación/rechazo, calendari zación
- ✅ **Disponibilidad de Programadores**: Slots de horario con validación de solapamientos
- ✅ **Notificaciones por Email**: Automáticas (creación, confirmación, rechazo)
- ✅ **Recordatorios Programados**: 15 minutos antes del inicio de asesoría
- ✅ **Reportes Dashboard**: Estadísticas JSON por status, programador y día
- ✅ **Exportación PDF/Excel**: Asesorías y proyectos exportables
- ✅ **Documentación OpenAPI**: Swagger UI accesible
- ✅ **Health Checks**: Actuator con métricas Prometheus
- ✅ **Base de Datos**: PostgreSQL con JPA/Hibernate
- ✅ **Preparado para Despliegue**: Docker, Railway, Render

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

#### Producción (application-prod.yml)

Configurar las siguientes variables de entorno:

```bash
# Base de datos
export DATABASE_URL=jdbc:postgresql://host:port/database
export DB_USERNAME=your-db-user
export DB_PASSWORD=your-db-password

# JWT
export JWT_SECRET=your-production-secret-key
export JWT_EXPIRATION=1800000
export JWT_REFRESH_EXPIRATION=604800000

# Email (SMTP)
export EMAIL_ENABLED=true
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
export MAIL_FROM=noreply@yourapp.com

# Frontend CORS
export FRONTEND_URL=https://your-frontend.app
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

### Railway

1. Crear nuevo proyecto en [Railway](https://railway.app)
2. Agregar PostgreSQL desde el marketplace
3. Agregar servicio desde repositorio GitHub
4. Configurar variables de entorno (ver sección de configuración)
5. Railway detecta el Dockerfile automáticamente

### Render

1. Crear Web Service en [Render](https://render.com)
2. Conectar repositorio GitHub
3. Configurar:
   - **Build Command**: `./gradlew bootJar`
   - **Start Command**: `java -jar build/libs/app.jar`
   - **Environment**: Agregar variables de entorno
4. Crear PostgreSQL database desde Render
5. Deploy automático en cada push a main

### Docker Manual

```bash
# Construir imagen
docker build -t exam-final-app .

# Ejecutar (con variables de entorno)
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://... \
  -e DB_USERNAME=... \
  -e DB_PASSWORD=... \
  -e JWT_SECRET=... \
  -e EMAIL_ENABLED=true \
  -e MAIL_HOST=... \
  -e MAIL_USERNAME=... \
  -e MAIL_PASSWORD=... \
  exam-final-app
```

## 🧪 Testing

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests de un paquete específico
./gradlew test --tests com.example.demo.asesorias.*

# Ver reporte de cobertura
./gradlew test jacocoTestReport
```

## 📧 Configuración de Email (Gmail)

Para usar notificaciones por email con Gmail:

1. Habilitar "Verificación en 2 pasos" en tu cuenta Google
2. Generar "Contraseña de aplicación":
   - Ir a: https://myaccount.google.com/apppasswords
   - Seleccionar "Correo" y generar
3. Configurar variables:
   ```bash
   MAIL_USERNAME=tu-email@gmail.com
   MAIL_PASSWORD=tu-contraseña-de-aplicacion
   MAIL_FROM=tu-email@gmail.com
   EMAIL_ENABLED=true
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

## 📝 Licencia

MIT License - ver [LICENSE](LICENSE) para más detalles.

## 👨‍💻 Autor

**Miguel V**  
- GitHub: [@miguelv145](https://github.com/miguelv145)
- Frontend: [https://miguelv145.github.io](https://miguelv145.github.io)

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
