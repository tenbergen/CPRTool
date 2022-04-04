#!/bin/bash

# Remove the previous instances.
docker container stop cpr-frontend && docker container rm cpr-frontend
docker container stop cpr-login && docker container rm cpr-login
docker container stop cpr-course-manager && docker container rm cpr-course-manager
docker container stop cpr-course-viewer && docker container rm cpr-course-viewer
docker container stop cpr-peer-review-teams && docker container rm cpr-peer-review-teams
docker container stop cpr-professor-assignment && docker container rm cpr-professor-assignment
docker container stop cpr-nginx && docker container rm cpr-nginx

# cd into the working directory.
cd app/ || exit

# Build the project using docker-compose and start the containers.
docker-compose -f "docker-compose.yml" up -d --build

# Prune any dangling images.
docker image prune

# Prune any dangling volumes.
docker volume prune

# Set the permission of the mounted volume from "root" to "default" for write permission.
docker exec -u 0:0 cpr-professor-assignment chown -R 1001 /opt/ol/wlp/output/defaultServer/assignments/
