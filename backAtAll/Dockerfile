# ===== Etapa 1: build con Gradle Wrapper =====
FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /workspace

# Copiamos wrapper y metadata primero para cachear dependencias
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Instala dos2unix y arregla CRLF + permisos
RUN apk add --no-cache dos2unix \
 && dos2unix gradlew \
 && chmod +x gradlew

# (opcional) quick sanity check
RUN ./gradlew --version

# Copiamos el resto y construimos el jar
COPY . .
RUN dos2unix gradlew && chmod +x gradlew
# Convierte line endings de archivos Java a LF para evitar errores de compilación
RUN find src -name "*.java" -exec dos2unix {} \;
# Limita memoria de Gradle y muestra logs detallados
ENV GRADLE_OPTS="-Xmx256m -Dfile.encoding=UTF-8"
RUN ./gradlew --no-daemon --stacktrace --info bootJar -x test


# ===== Etapa 2: runtime =====
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar

ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["sh","-c","java -Dserver.port=${PORT} $JAVA_OPTS -jar app.jar"]
