FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml first (for caching dependencies)
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the built jar from the previous stage
COPY --from=builder /app/target/githubscore-0.0.1-SNAPSHOT.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Set default active profile (optional)
ENV SPRING_PROFILES_ACTIVE=default

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
