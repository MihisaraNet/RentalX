# ------------------------------------------------------------------------------
# Stage 1: Build Artifact with Maven and OpenJDK 21
# ------------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and package application
COPY src ./src
RUN mvn clean package -DskipTests -B

# ------------------------------------------------------------------------------
# Stage 2: Runtime Container with Slim Temurin JRE 21
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root system user for security
RUN addgroup -S rentalx && adduser -S rentalx -G rentalx

# Create storage directory for uploads and data
RUN mkdir -p /app/data /app/src/main/resources/static/uploads && \
    chown -R rentalx:rentalx /app

# Copy executable jar from builder stage
COPY --from=builder --chown=rentalx:rentalx /build/target/rentalX-0.0.1-SNAPSHOT.jar app.jar

USER rentalx
EXPOSE 8080

# Production JVM optimizations
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
