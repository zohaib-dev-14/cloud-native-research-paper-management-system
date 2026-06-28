# ==========================================
# Stage 1: Build Stage (Heavy JDK)
# ==========================================
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .

# Docker Cache utilization for faster builds
RUN mvn dependency:go-offline

COPY src ./src

# Building the final fat JAR
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Runtime Stage (Slim JRE - Glibc Safe)
# ==========================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# 🔒 1. Security: Root user avoidance
# Ubuntu/Debian base par user banane ka standard tareeqa
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Copying only the single final app.jar (No asterisks, no snapshot confusion!)
COPY --from=builder /app/target/app.jar app.jar

# Changing ownership of the file to our new non-root user
RUN chown appuser:appgroup app.jar

# Switching to non-root user for execution
USER appuser

EXPOSE 8080

# 🏥 2. Healthcheck: Spring Boot Actuator Integration
# Docker har 30s baad check karega ke Actuator endpoint status 200 (OK) de raha hai ya nahi
HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-jar","app.jar"]