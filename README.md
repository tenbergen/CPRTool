# Calibrated Peer Review

A highly scalable web application that assists the process of coordinating and evaluating peer reviews of student work.

## Running the Project

This web application is made to be built on bare metal Linux environment using Docker. Make sure that the following software are installed:
- [Docker](https://docs.docker.com/engine/install/)
- [Docker Compose](https://docs.docker.com/compose/install/)

**Step 1:** Clone the repository.

**Step 2:** At the root of the directory, create a `.env` file using `.env.example` as the template and fill out the variables.

- Head to [Google Cloud Console Credentials](https://console.cloud.google.com/apis/credentials) to create an `OAuth client ID`.
- Set the `Application type` as `Web application`. `Application name` can be anything.
- Add the full domain URL used in `.env` (with protocol and port if used) to `Authorized JavaScript origins` and `Authorized redirect URIs`. Save the changes.
- Copy and paste the `Client ID` and `Client secret` to your `.env` file as `CLIENT_ID` and `CLIENT_SECRET` respectively.

**Step 3:** Execute `sh build-app.sh` to build and start the Docker containers.

**Step 4:** In `scripts` directory, execute `mongo-init.sh` to initialize user database login credentials. This is only necessary on the very first build of the application.

The web application should be running on the specified domain in your `.env` file.

Optionally, in `scripts` directory:
- If you wish to change the user password, execute `mongo-changepwd.sh`. 
- To delete everything from all databases as well as saved files, execute `delete-db.sh`.

**Step 5:** To add users with elevated privileges (a.k.a professors), include the user's email on separate lines in `professor-list.txt`.

## Local Development Environment

For local development environment, a Docker setup is not necessary. Make sure that the following software and dependencies are installed:
- [Maven](https://maven.apache.org/install.html) >= 3.8.4
- [JDK](https://openjdk.java.net/projects/jdk/17/) >= 17
- [MongoDB](https://www.mongodb.com/docs/manual/installation/) >= 5.0
- [MongoDB Compass](https://www.mongodb.com/products/compass) is optional but recommended for a MongoDB GUI that also comes with a terminal for shell commands

**Step 1:** On Windows, hit `Windows + R`, enter `sysdm.cpl` and navigate to `Advanced -> Environment Variables...` and add the following variables:
| Variable         | Value       |
|------------------|-------------|
| `MONGO_HOSTNAME` | `localhost` |
| `MONGO_PORT`     | `27017`     |
| `MONGO_DATABASE` | `cpr`       |
| `MONGO_USERNAME` | Your choice |
| `MONGO_PASSWORD` | Your choice |

On Linux-based operating systems, you may achieve the similar result by executing `EXPORT <NAME>=<VALUE>`. 

**Step 2:** Using the MongoDB shell (either using MongoDB shell from terminal or the built-in one in MongoDB Compass) to create authentication for the database by running the following command:

- `db.createUser({user: "<your username>", pwd: "<your password>", roles: [{role: "readWrite", db: "cpr"}]});`

**Step 3:** Run `mvn liberty:dev` to start the project in developer mode. The web app should be running on http://localhost:xxxxx - the port depends on which microservice you are running as following:
| Microservice                     | Port    |
|----------------------------------|---------|
| `frontend`                       | `13125` |
| `login`                          | `13126` |
| `course-manager`                 | `13127` |
| `course-viewer`                  | `13128` |
| `peer-review-teams`              | `13129` |
| `professor-assignment`           | `13130` |
| `student-assignment`             | `13131` |
| `student-peer-review-assignment` | `13132` |

## Contributing

Contributors are more than welcome to improve the project by creating a new issue to report bugs, suggest new features, or make changes to the source code by making a pull request. To have your work merged in, please make sure the following is done:

1. Fork the repository and create your branch from master.
2. If you’ve fixed a bug or added something new, add a comprehensive list of changes.
3. Ensure that your code is tested, functional, and is linted.
