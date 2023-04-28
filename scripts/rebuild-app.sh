#!/bin/bash
# Back out one level to access docker-compose.yml.
cd ..

# Rebuild the microservices barring the databases and the nginx container
docker-compose -f "docker-compose.yml" up -d --build login
docker-compose -f "docker-compose.yml" up -d --build course-manager
docker-compose -f "docker-compose-rebuild.yml" up -d --build course-viewer
docker-compose -f "docker-compose-rebuild.yml" up -d --build peer-review-teams
docker-compose -f "docker-compose-rebuild.yml" up -d --build professor-assignment
docker-compose -f "docker-compose-rebuild.yml" up -d --build student-assignment
docker-compose -f "docker-compose-rebuild.yml" up -d --build student-peer-review-assignment
docker-compose -f "docker-compose-rebuild.yml" up -d --build frontend

# Prune any dangling images.
docker image prune -f

# Prune any dangling volumes.
docker volume prune -f
