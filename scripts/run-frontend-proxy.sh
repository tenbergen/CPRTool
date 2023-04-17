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

# Back out one level to access docker-compose.yml.
cd ..

# Build the project using docker-compose and start the container.
docker-compose -f "docker-compose-local-frontend.yml" up -d --build