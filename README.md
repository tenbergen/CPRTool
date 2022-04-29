# Calibrated Peer Review

A highly scalable web application that assists the process of coordinating and evaluating peer reviews of student work.

## Running the Project

This web application is made to be built on bare metal Linux environment using Docker. Make sure that the following softwares are installed:
- [Docker](https://docs.docker.com/engine/install/)
- [Docker Compose](https://docs.docker.com/compose/install/)

**Step 1:** Clone the repository.

**Step 2:** At the root of the directory, create a `.env` file using `.env.example` as the template and fill out the variables.

**Step 3:** Run `sh build-app.sh` to build and start the Docker containers.

If the database login credentials that you specified in `.env` were not being setup automatically on your first run, you may do so manually by executing:
1. `docker exec -it <database container> bash`
2. Execute the content from `mongo-init.sh`

The web application should be running on the specified domain in your `.env` file.

## Local Development Environment

For local development environment, a Docker setup is not necessary. Make sure that the following softwares and dependencies are installed:
- [Maven](https://maven.apache.org/install.html) >= 3.8.4
- [JDK](https://openjdk.java.net/projects/jdk/17/) >= 17
- [MongoDB](https://www.mongodb.com/docs/manual/installation/) >= 5.0
- [MongoDB Compass](https://www.mongodb.com/products/compass) is optional but recommended for a MongoDB GUI that also comes with a terminal for shell commands

**Step 1:** On Windows, hit `Windows + R`, enter `sysdm.cpl` and navigate to `Advanced -> Environment Variables...` and add the following variables:
- `MONGO_HOSTNAME` - `localhost`
- `MONGO_PORT` - `27017`
- `MONGO_DATABASE` - `cpr`
- `MONGO_USERNAME` - Your choice
- `MONGO_PASSWORD` - Your choice

On Linux-based operating systems, you may achieve the similar result by executing `EXPORT <NAME>=<VALUE>`. 

**Step 2:** Using the MongoDB shell (either using MongoDB shell from terminal or the built-in one in MongoDB Compass) to create authentication for the database by running the following command:

- `db.createUser({user: "<your username>", pwd: "<your password>", roles: [{role: "readWrite", db: "cpr"}]});`

**Step 3:** Run `mvn liberty:dev` to start the project in developer mode. The web app should be running on http://localhost:xxxxx - the port depends on which microservice you are running as following:
- Frontend: 13125
- Login: 13126
- Course manager: 13127
- Course viewer: 13128
- Peer review teams: 13129
- Professor assignment: 13130
- Student assignment: 13131
- Student peer review assignment: 13132

## Contributing

Contributors are more than welcome to improve the project by creating a new issue to report bugs, suggest new features, or make changes to the source code by making a pull request. To have your work merged in, please make sure the following is done:

1. Fork the repository and create your branch from master.
2. If you’ve fixed a bug or added something new, add a comprehensive list of changes.
3. Ensure that your code is tested, functional, and is linted.
