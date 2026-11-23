FROM maven:3.9-eclipse-temurin-17 AS build

# Copy the Maven wrapper and dependencies first to improve build cache reuse.
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Compile and package the service inside the image build context.
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre

# Run the application from a minimal runtime image.
WORKDIR /app
COPY --from=build /app/target/Eureka-Service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]