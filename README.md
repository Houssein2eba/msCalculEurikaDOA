

# Architecture Microservices et Préparation du Pipeline DevOps
## Objectif
préparer la mise en place d'un pipeline DevOps à travers le déploiement et l'exécution d'une architecture microservices basée sur Spring Boot et Spring Cloud (vue en MPI1)
Cette activité permettra aux étudiants de (d') :
Utiliser Git et GitHub pour la gestion du code source.
Se rappeler du fonctionnement d'une architecture microservices et 
Se rappeler de la mise en œuvre un serveur de découverte de services avec Eureka.
Se rappeler de la configuration d'une passerelle d'accès avec Spring Cloud Gateway.
Construire des applications Java avec Maven.
Générer les fichiers exécutables JAR.
Vérifier la communication entre les différents microservices.
Préparer les étapes du pipeline DevOps : Build, Test, Package et Déploiement.
Préparer l'intégration future de Docker et des outils CI/CD.
Cette activité constitue la première étape vers la mise en œuvre d'une chaîne d'intégration et de déploiement continus (CI/CD).

### Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- Java JDK 17 ou version supérieure
- Apache Maven 3.9 ou version supérieure
- Git
- IntelliJ IDEA ou Visual Studio Code

# CODE
Développement des microservices :
Eureka-Service
Calculatrice-Service
CalculStatistique-Service
Gateway-Service

Dans votre cas, vous les avez développés l'année dernière.

Je vous les repartage via ce dépôt GitHub afin que vous puissiez les conserver dans votre espace, au cas où le travail réalisé l'année dernière aurait été perdu.


Avec IntelliJ IDEA : Ouvrir le projet puis exécuter la classe principale du microservice à l'aide du bouton Run.

Avec VS Code : installez les extensions suivantes : Extension Pack for Java ; Spring Boot Extension Pack ; Maven for Java

Sans IDE :

### Linux / macOS	
    ./mvnw clean install
    chmod +x mvnw ./mvnw spring-boot:run

  
### Windows (CMD)	
    mvnw.cmd clean install
    mvnw.cmd spring-boot:run
    
# BUILD
    mvn clean package
 Chaque étudiant construire le fichier JAR pour chaque microservice
des fichiers JAR seront créer
Par exemple : Calculatrice-Service-0.0.1-SNAPSHOT.jar
    
# TEST
   ## Lancer sans utilisation de conteneur

   java -jar target/Eureka-Service-0.0.1-SNAPSHOT.jar
   
      Eureka-Service
        http://localhost:8761
                http://localhost:8084/api/calcul/somme?a=10&b=20
                
      Calculatrice-Service  
        Rappel : en local activer mysql et si le tableau /calculdb1 existe effacer le a chaque test
        http://localhost:8084/api/calcul/somme?a=10&b=20
        
    CalculStatistique-Service
        http://localhost:8083/api/stats/moyenne?a=10&b=20
    Gateway-Service
        http://localhost:8181/calculatrice-service/api/calcul/somme?a=10&b=20


# RELEASE
Chaque microservice doit contenir :

Le fichier JAR généré précédemment sera utilisé pour conteneuriser et déployer l'application à l'aide de Docker.
Créer un Dockerfile pour chaque microservice au même niveau que le pom.xml


   ## Lancer avec utilisation de conteneur
    Remarque :il faut activer docker   
   
   Il faut buileder les images, il faut utiliser Dockerfile

#### Exemple Dockerfile
    FROM eclipse-temurin:17-jdk
    WORKDIR /app
    COPY target/*.jar app.jar
    EXPOSE 8080
    ENTRYPOINT ["java","-jar","app.jar"]

#### Construction de l'image Docker
       docker build -t calculatrice-service .

Par la suite ce build sera déclaré dans Docker Compose
       calculstatistique-service:
          build: ./CalculStatistique-Service
       
#### Vérification de l'image
    docker images

### Mode Bridge
   
        docker network ls
        docker network create micro-net
        docker run -d --network micro-net --name eureka-server -p 8761:8761 eureka-service
    
   ####  Eureka-Service
        http://localhost:8761
   #### Calculatrice-Service   
      
    docker run -d --network micro-net --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=calculdb1 mysql:8.0
    
    docker run -d --network micro-net --name calculatrice-service -p 8084:8084 -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/ -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/calculdb1?createDatabaseIfNotExist=true -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=root calculatrice-service
      
        http://localhost:8084/api/calcul/somme?a=10&b=20
        
   #### CalculStatistique-Service
    docker run -d --network micro-net --name calculstatistique-service -p 8083:8083 -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/ calculstatistique-service
    
        http://localhost:8083/api/stats/moyenne?a=10&b=20
  ####  Gateway-Service

        docker run -d --network micro-net --name gateway-service -p 8181:8181 -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/ gateway-service
        
        http://localhost:8181/calculatrice-service/api/calcul/somme?a=10&b=20

        
        docker inspect gateway-service | findstr EUREKA  
        
        docker exec -it gateway-service printenv
        
        ajouter dans dockerfile de gateway
 ####
    eureka:
      client:
        service-url:
          defaultZone: http://localhost:8761/eureka/
        
        docker rm -f gateway-service
        docker ps -a
        docker rmi -f gateway-service
        docker image ls
       
       cd ga*
       mvn clean package -DskipTests
       dir target
       docker build -t gateway-service .
       docker image ls
    

        docker run -d --network micro-net --name gateway-service -p 8181:8181 -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/ gateway-service

        docker logs -f gateway-service

        http://localhost:8181/calculatrice-service/api/calcul/somme?a=10&b=20
        http://localhost:8181/calculstatistique-service/api/stats/moyenne?a=10&b=20

        http://localhost:8181/actuator/gateway/routes

#### Arrêter les conteneurs lancés manuellement
    docker rm -f gateway-service calculstatistique-service calculatrice-service mysql eureka-server
    


# DEPLOY
Le fichier docker-compose.yml est unique et placé à la racine.

## Exemple docker-compose.yml
    docker compose up -d
    
les conteneurs seront réellement lancés.

## d'autres cmd
    docker restart calculatrice-service
    docker exec -it gateway-service printenv
    docker compose logs gateway-service
    docker logs gateway-service
    docker compose build --no-cache gateway-service
    docker compose stop  
    docker compose down  



# PIPLINE CI

# GitHub Actions permet d'automatiser les étapes précédentes : Build, Test, Release, Déploiement
compile les 4 projets ;
construit les images Docker ;
lance les conteneurs ;
affiche les logs d'Eureka ;
arrête les conteneurs.

Depuis la racine du projet :


mkdir .github\workflows
Puis créez le fichier :
 .github\workflows\ci.yml

 git remote -v
Sinon
git remote set-url origin https://github.com/Zayanifss/msCalculEurikaDOA.git

Récupérer le token profil / SettingsDeveloper Settings / token classic / generete / New personal access token (classic /repo
git branch

git pull origin main --allow-unrelated-histories

git status


git add .
git commit -m "Ajout GitHub Actions CI"
git push

Ouvrez le dépôt msCalculEurikaDOA-CI
Cliquez sur Actions

## Remarque
À partir du pipeline DevOps

Dans GitHub Actions, GitLab CI ou Jenkins, le pipeline peut générer automatiquement :

la version du JAR
l'image Docker créée
les commandes de déploiement
les rapports de tests

et publier ces informations sous forme d'artefacts ou de pages web.

mvn test
→ docker build
→ docker push
→ docker compose up




# OPERATE
Une fois l'application déployée, il faut l'exploiter :
démarrage
arrêt
redémarrage
consultation des logs

## Exemples :
    docker compose ps
    docker compose logs
    docker compose restart

# MONITOR

# CD = Continuous Delivery / Continuous Deployment