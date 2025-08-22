# Equivaut au Docker pull, on va chercher l'image de base du DockerHub
FROM openjdk:26-slim

# Documenter l'image
LABEL author="Guillaume"
LABEL created_at="2025-08-21"
LABEL stack="java"


# Installer les certificats nécessaires
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates p11-kit \
    && rm -rf /var/lib/apt/lists/*

# Crée un groupe système nommé 'appuser' (-r) et un utilisateur système “limité”, \
# sans privilèges d’administration (non-root), avec les caractéristiques suivantes
RUN groupadd -r appuser && useradd -r -g appuser appuser


WORKDIR /app

# Copier le JAR construit dans /app
COPY target/step-by-step-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port de l'API (par défaut 8080)
EXPOSE 8080

# Commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
