FROM openjdk:21-jdk-alpine AS builder

ENV SPRING_PROFILES_ACTIVE=docker
WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN chmod +x mvnw

COPY src src

RUN ./mvnw clean package -DskipTests

FROM openjdk:21-jdk-slim

RUN groupadd -r -g 1000 catapi && \
    useradd -r -u 1000 -g catapi catapi

ENV SPRING_PROFILES_ACTIVE=docker
WORKDIR /app

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/*.jar app.jar

RUN chown -R catapi:catapi /app

USER catapi

EXPOSE 8090

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8090/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]

