# ---------- Etapa 1: Build con Gradle ----------
FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /workspace

# Copiamos lo mínimo para cachear dependencias primero
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew
# Descarga dependencias (no compila fuentes todavía)
RUN ./gradlew --no-daemon dependencies || true

# Copiamos el código y construimos el jar
COPY src ./src
RUN ./gradlew --no-daemon bootJar -x test

# ---------- Etapa 2: Runtime ligero ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiamos el JAR construido
# (Ajusta el patrón si tu artefacto no es -SNAPSHOT)
COPY --from=builder /workspace/build/libs/*-SNAPSHOT.jar app.jar

# Variables opcionales
ENV JAVA_OPTS=""
ENV SERVER_PORT=8080

EXPOSE 8080
# Permitimos pasar JAVA_OPTS desde el panel de Render si quieres (Xms/Xmx, etc.)
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
