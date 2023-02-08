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