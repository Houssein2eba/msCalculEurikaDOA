# msCalculEurikaDOA

A Java 17 microservices project with Eureka service discovery, a Spring Cloud Gateway, and MySQL. Docker Compose runs the complete application. GitHub Actions tests the services and publishes Docker images to Docker Hub after successful checks on `main`.

## Services and local URLs

| Service | Port | Example |
| --- | ---: | --- |
| Eureka registry | 8761 | <http://localhost:8761> |
| Calculator | 8084 | <http://localhost:8084/api/calcul/somme?a=2&b=3> |
| Statistics | 8083 | <http://localhost:8083/api/stats/moyenne?a=2&b=4> |
| API gateway | 8181 | <http://localhost:8181/actuator/health> |

Through the gateway:

- Calculator: <http://localhost:8181/calculatrice-service/api/calcul/somme?a=2&b=3>
- Statistics: <http://localhost:8181/calculstatistique-service/api/stats/moyenne?a=2&b=4>

## Requirements

- Git
- Java JDK 17
- Docker Desktop with the Linux container engine and Docker Compose v2
- A terminal (the commands below use Windows PowerShell)

Each service includes a Maven wrapper, so a separate Maven installation is not required.

## 1. Clone the repository

```powershell
git clone https://github.com/Houssein2eba/msCalculEurikaDOA.git
cd msCalculEurikaDOA
```

## 2. Run the services locally

This runs the Java services on your computer and uses a MySQL container for the database.

1. Start Docker Desktop and start MySQL from the repository root:

   ```powershell
   docker compose up -d mysql
   ```

2. Open four PowerShell terminals in the repository. Start Eureka first:

   ```powershell
   cd .\Eureka-Service
   .\mvnw.cmd spring-boot:run
   ```

3. In a second terminal, start the calculator. Its database settings match the MySQL container above:

   ```powershell
   cd .\Caculatrice-Service
   $env:DB_URL = "jdbc:mysql://localhost:3307/calculdb1?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true"
   $env:DB_USERNAME = "root"
   $env:DB_PASSWORD = "root"
   .\mvnw.cmd spring-boot:run
   ```

4. In a third terminal, start the statistics service:

   ```powershell
   cd .\CalculStatistique-Service
   .\mvnw.cmd spring-boot:run
   ```

5. In a fourth terminal, start the gateway:

   ```powershell
   cd .\Gateway-Service
   .\mvnw.cmd spring-boot:run
   ```

The services use `http://localhost:8761/eureka/` by default. Allow time for Eureka and the services to start and register before testing the gateway URLs above.

To stop the local Java services, press `Ctrl+C` in each terminal. Stop MySQL with:

```powershell
docker compose stop mysql
```

## 3. Run the complete application with Docker

From the repository root, with Docker Desktop running:

```powershell
docker compose up --build -d
docker compose ps
```

Open the service URLs listed above. To inspect logs:

```powershell
docker compose logs -f
```

Stop the stack and keep the MySQL data:

```powershell
docker compose down
```

To also delete the MySQL data volume and start with an empty database next time:

```powershell
docker compose down --volumes
```

Compose builds local images with a `dev` tag by default. No `.env` file is required for the default local setup.

## 4. Set up a self-hosted GitHub Actions runner (Windows)

The workflow targets a runner labelled `self-hosted`, `Windows`, and `X64`. Set it up on a Windows machine that has:

- JDK 17 installed, with `JAVA_HOME` configured
- Docker Desktop running in Linux container mode
- Network access to GitHub, Maven repositories, and Docker Hub

The Maven wrapper is included in each service. Confirm Docker is available in the runner's Windows account:

```powershell
docker --version
docker compose version
docker info --format '{{.OSType}}'
```

The last command should print `linux`.

In the GitHub repository, open **Settings → Actions → Runners → New self-hosted runner**, select **Windows** and **x64**, then follow GitHub's displayed instructions to download, configure, and start the runner. Run the generated `config.cmd` command with the short-lived registration token from that page, then start the runner with:

```powershell
.\run.cmd
```

Keep the runner running while a workflow is queued or executing. Do not commit the registration token or runner credentials.

## 5. Configure Docker Hub publishing

In the repository, open **Settings → Secrets and variables → Actions → New repository secret** and add:

- `DOCKERHUB_USERNAME`: your Docker Hub username (`houssein2eba`)
- `DOCKERHUB_TOKEN`: a Docker Hub access token with read and write permissions

Do not put the token in the README, source code, or a committed `.env` file.

## 6. Run and check the CI/CD workflow

The workflow is [.github/workflows/ci.yaml](.github/workflows/ci.yaml).

- A pull request targeting `main` runs the Java tests and Docker integration checks but does not publish images.
- A push to `main` runs the same checks. If all checks pass, the workflow publishes the tested service images to `houssein2eba/mscalcul-eurika-doa`.
- You can also open **Actions → CI → Run workflow** and select `main`.

The image tags include the commit SHA, for example `eureka-<sha>` and `gateway-<sha>`, plus moving `*-latest` tags. Check the workflow run in the **Actions** tab for test and publish results.

If Docker Hub login fails with `unauthorized`, update the `DOCKERHUB_USERNAME` and `DOCKERHUB_TOKEN` secrets. The username must match the Docker Hub account, and the token must be valid and have write permission.
