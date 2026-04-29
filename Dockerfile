# -------- STAGE 1: build --------
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# Copia tudo
COPY . .

# Gera o JAR (sem testes)
RUN gradle bootJar -x test

# -------- STAGE 2: runtime --------
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copia só o JAR gerado
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]