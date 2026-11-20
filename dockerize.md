# Dockerize the microservices

This guide runs every application in its own container:

| Service | Folder | Container port | Host port |
|---|---|---:|---:|
| Eureka | `Eureka-Service` | 8761 | 8761 |
| Gateway | `Gateway-Service` | 8181 | 8181 |
| CalculStatistique | `CalculStatistique-Service` | 8083 | 8083 |
| Caculatrice | `Caculatrice-Service` | 8084 | 8084 |
| MySQL | Docker image | 3306 | 3306 |

The containers share a Docker network created by Compose. Inside that network,
services must use container names, not `localhost`:

- Eureka: `http://eureka-server:8761/eureka/`
- MySQL: `jdbc:mysql://mysql:3306/calculdb1`

`localhost` inside a container means that same container.

## 1. Prerequisites

Install and start Docker Desktop for Windows. Verify it from PowerShell:

```powershell
docker --version
docker compose version
```

Run all commands below from the repository root:

```text
C:\Users\HP\Documents\devops\msCalculEurikaDOA-main\msCalculEurikaDOA-main
```

## 2. Add one Dockerfile per service

Create `Eureka-Service\Dockerfile`:

```dockerfile
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Create `Gateway-Service\Dockerfile`:

```dockerfile
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8181
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Create `CalculStatistique-Service\Dockerfile`:

```dockerfile
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Create `Caculatrice-Service\Dockerfile`:

```dockerfile
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
```

These are multi-stage builds: Maven and the source code exist only in the
temporary build image; the final containers contain only Java 17 and the
compiled application.

## 3. Add `.dockerignore` files

Create the same `.dockerignore` file in each service folder:

```text
target/
.idea/
*.iml
*.log
.git/
```

This prevents old IntelliJ output and old JAR files from being copied into the
Docker build context.

## 4. Create `compose.yaml` in the repository root

Create `compose.yaml` beside the service folders:

```yaml
services:
  mysql:
    image: mysql:8.4
    container_name: mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: calculdb1
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-uroot", "-proot"]
      interval: 10s
      timeout: 5s
      retries: 10

  eureka-server:
    build:
      context: ./Eureka-Service
    container_name: eureka-server
    restart: unless-stopped
    ports:
      - "8761:8761"

  calculstatistique-service:
    build:
      context: ./CalculStatistique-Service
    container_name: calculstatistique-service
    restart: unless-stopped
    environment:
      SERVER_PORT: 8083
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
    ports:
      - "8083:8083"
    depends_on:
      eureka-server:
        condition: service_started

  caculatrice-service:
    build:
      context: ./Caculatrice-Service
    container_name: caculatrice-service
    restart: unless-stopped
    environment:
      SERVER_PORT: 8084
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
      SPRING_DATASOURCE_URL: "jdbc:mysql://mysql:3306/calculdb1?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true"
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
    ports:
      - "8084:8084"
    depends_on:
      mysql:
        condition: service_healthy
      eureka-server:
        condition: service_started

  gateway-service:
    build:
      context: ./Gateway-Service
    container_name: gateway-service
    restart: unless-stopped
    environment:
      SERVER_PORT: 8181
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
    ports:
      - "8181:8181"
    depends_on:
      eureka-server:
        condition: service_started
      calculstatistique-service:
        condition: service_started
      caculatrice-service:
        condition: service_started

volumes:
  mysql-data:
```

The Compose service name is also a DNS name on the internal network. For
example, `caculatrice-service` can reach MySQL at `mysql:3306`.

## 5. Build and start everything

From the repository root:

```powershell
docker compose build
docker compose up -d
```

Check that all containers are running:

```powershell
docker compose ps
```

Open the Eureka dashboard:

```text
http://localhost:8761
```

The gateway is available at:

```text
http://localhost:8181
```

The individual services are available from the host at ports 8083 and 8084.
For normal use, prefer calling the gateway.

## 6. View logs and troubleshoot

Show logs for all services:

```powershell
docker compose logs -f
```

Show one service:

```powershell
docker compose logs -f gateway-service
docker compose logs -f caculatrice-service
```

Check the environment and network:

```powershell
docker compose exec caculatrice-service sh
docker compose exec caculatrice-service getent hosts mysql
docker compose exec calculstatistique-service getent hosts eureka-server
```

If a service starts before Eureka has finished booting, wait a few seconds and
check the logs. Spring Cloud Eureka clients normally retry registration.

## 7. Stop, restart, and rebuild

Stop containers while preserving the MySQL data volume:

```powershell
docker compose down
```

Rebuild after changing Java code:

```powershell
docker compose up -d --build
```

Stop containers and delete the MySQL data volume as well:

```powershell
docker compose down -v
```

Use `down -v` only when intentionally resetting the database. It permanently
deletes the Docker-managed MySQL data for this Compose project.

## 8. IntelliJ workflow

Use IntelliJ for editing and debugging, and Docker Compose for running the
complete system:

1. Stop locally running Spring Boot configurations to avoid port conflicts.
2. Start Docker Desktop.
3. Run `docker compose up -d --build` in the IntelliJ terminal.
4. Read logs with `docker compose logs -f <service-name>`.
5. Stop the environment with `docker compose down`.

Do not use `localhost` in container-to-container URLs. Use the Compose service
names shown above.

## Important note about the current Maven projects

The Dockerfiles use JDK 17, matching the `<java.version>17</java.version>`
configuration in the Maven projects. This also avoids the IntelliJ/JDK 27
compiler and Lombok initialization problem described in `fix.md`.

If a service folder does not contain `.mvn`, remove the `COPY .mvn .mvn` line
and use the system Maven command in the builder stage instead:

```dockerfile
RUN mvn -B dependency:go-offline
RUN mvn -B clean package -DskipTests
```
