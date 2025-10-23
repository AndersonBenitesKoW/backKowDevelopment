# ---------- Etapa 1: Build con Gradle ----------
# ===== Etapa 1: build con Gradle Wrapper =====
FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /workspace

# Copiamos wrapper y metadata primero para cachear dependencias
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Corrige CRLF de Windows para evitar "gradlew: not found"
RUN chmod +x gradlew && sed -i 's/\r$//' gradlew

# Descarga deps (para cache)
RUN ./gradlew --no-daemon dependencies || true

# Copiamos el resto y construimos el jar
COPY . .
RUN ./gradlew --no-daemon bootJar -x test

# ===== Etapa 2: runtime ligero =====
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiamos el jar construido
COPY --from=builder /workspace/build/libs/*.jar app.jar

# Render puede inyectar PORT; si no, usamos 8080
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh","-c","java -Dserver.port=${PORT} $JAVA_OPTS -jar app.jar"]
