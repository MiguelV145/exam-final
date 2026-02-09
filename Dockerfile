# =========================
# Stage 1: Build
# =========================
FROM gradle:jdk17 AS build
WORKDIR /app

# Copiar archivos de configuración para cachear dependencias
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon

# Copiar el código fuente
COPY src ./src

# Construir el JAR ejecutable (sin tests)
RUN gradle bootJar -x test --no-daemon

# =========================
# Stage 2: Runtime
# =========================
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar el JAR generado
COPY --from=build /app/build/libs/app.jar app.jar

# Puerto estándar para contenedores / Render
EXPOSE 8080

# Ejecutar la aplicación con PORT dinámico para Render
CMD java -Dserver.port=${PORT:-8080} -jar app.jar