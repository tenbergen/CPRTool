# Stage 1: Build the project and package it into a .jar file.
FROM maven:3.8.4 as maven

WORKDIR .
COPY . .

ARG URL
ARG CLIENT_ID
ENV URL=$URL
ENV CLIENT_ID=$CLIENT_ID

RUN mvn package

# Stage 2: Copy the .jar file and server.xml into the required location.
FROM icr.io/appcafe/open-liberty:full-java11-openj9-ubi

# Copy from the intermediate build container.
COPY --from=maven src/main/liberty/config/server.xml /config/
COPY --from=maven target/*.war /config/apps
