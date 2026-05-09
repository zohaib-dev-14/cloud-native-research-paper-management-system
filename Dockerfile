## BUILD STAGE ##
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

## RUN STAGE ##
FROM eclipse-temurin:17

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENV APP_NAME=ResearchPaperSystem

ENTRYPOINT ["java", "-jar", "app.jar"]



