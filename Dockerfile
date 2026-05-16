# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Cache dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline

# Build the application
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:17-jre-alpine

# Create non-root user
RUN addgroup -S chess && adduser -S chess -G chess

WORKDIR /app

# Copy JAR from build stage
COPY --from=build --chown=chess:chess /app/target/chess-game-0.0.1-SNAPSHOT.jar ./app.jar

# Security: drop capabilities, run as non-root
USER chess:chess

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/chess/ || exit 1

# Metadata
LABEL \
  org.opencontainers.image.title="chess-game" \
  org.opencontainers.image.description="Chess game backend - Spring Boot application" \
  org.opencontainers.image.java.version="17" \
  org.opencontainers.image.source="https://github.com/your-org/chess-backend"

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
