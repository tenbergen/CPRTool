#!/bin/bash

# Remove the previous instances.
docker container stop cpr-frontend && docker container rm cpr-frontend
docker container stop cpr-login && docker container rm cpr-login
docker container stop cpr-course-manager && docker container rm cpr-course-manager
docker container stop cpr-course-viewer && docker container rm cpr-course-viewer
docker container stop cpr-professor-assignment && docker container rm cpr-professor-assignment
docker container stop cpr-peer-review-teams && docker container rm cpr-peer-review-teams
docker container stop cpr-student-assignment && docker container rm cpr-student-assignment
docker container stop cpr-student-peer-review-assignment && docker container rm cpr-student-peer-review-assignment
docker container stop cpr-nginx && docker container rm cpr-nginx
docker container stop cpr-mongo && docker container rm cpr-mongo
docker container stop cpr-mongo2 && docker container rm cpr-mongo2
docker container stop cpr-mongo3 && docker container rm cpr-mongo3
docker container stop cpr-mongo4 && docker container rm cpr-mongo4
docker container stop cpr-mongo5 && docker container rm cpr-mongo5

# Back out one level to access docker-compose.yml.
cd ..

# Build the project using docker-compose and start the containers.
docker-compose -f "docker-compose.yml" up -d --build

# Set the permission of the mounted volume from "root" to "default" for write permission.
docker exec -u 0:0 cpr-professor-assignment chown 1001 /opt/ol/wlp/output/defaultServer/assignments/
docker exec -u 0:0 cpr-student-assignment chown 1001 /opt/ol/wlp/output/defaultServer/assignments/
docker exec -u 0:0 cpr-student-peer-review-assignment chown 1001 /opt/ol/wlp/output/defaultServer/assignments/

# Prune any dangling images.
docker image prune -f

# Prune any dangling volumes.
docker volume prune -f
